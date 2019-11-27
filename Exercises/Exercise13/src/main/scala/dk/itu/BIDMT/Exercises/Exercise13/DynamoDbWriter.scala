//code source: https://docs.databricks.com/spark/latest/structured-streaming/examples.html#write-to-amazon-dynamodb-using-foreach-in-scala-and-python


package dk.itu.BIDMT.Exercises.Exercise13


import java.util

import org.apache.spark.sql.{ForeachWriter, Row}
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException
import java.util.ArrayList

import scala.collection.JavaConverters._


class DynamoDbWriter(tableName: String, regionName: String) extends ForeachWriter[Row] {


  // This will lazily be initialized only when open() is called
  lazy val ddb = AmazonDynamoDBClientBuilder.standard()
    .withCredentials(new DefaultAWSCredentialsProviderChain())
    .withRegion(regionName)
    .build()

  //
  // This is called first when preparing to send multiple rows.
  // Put all the initialization code inside open() so that a fresh
  // copy of this class is initialized in the executor where open()
  // will be called.
  //
  def open(partitionId: Long, epochId: Long) = {
    ddb  // force the initialization of the client
    true
  }

  //
  // This is called for each row after open() has been called.
  // This implementation sends one row at a time.
  // A more efficient implementation can be to send batches of rows at a time.
  //
  def process(row: Row) = {
    val rowAsMap = row.getValuesMap(row.schema.fieldNames)
    val dynamoItem: util.Map[String, AttributeValue] = rowAsMap.mapValues {
      v: Any => new AttributeValue(v.toString)
    }.asJava

    ddb.putItem(tableName, dynamoItem)
  }

  //
  // This is called after all the rows have been processed.
  //
  def close(errorOrNull: Throwable) = {
    ddb.shutdown()
  }
}
