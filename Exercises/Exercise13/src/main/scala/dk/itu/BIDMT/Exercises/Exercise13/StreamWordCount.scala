package dk.itu.BIDMT.Exercises.Exercise13

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}
import org.apache.spark.sql.functions._

object StreamWordCount {

  val spark = SparkSession
    .builder()
    .appName("StreamWordCountApp")
    //.master("local")
    .getOrCreate()

  import spark.implicits._

  def dataLoader(path: String):DataFrame ={
    spark
      .readStream
      .textFile(path)
      .toDF

  }


  def main(args: Array[String]): Unit = {

    if(args.length < 3){
      println("Usage: StreamWordCount streamInputPath regionName dynamoTableName")
      System.exit(1)
    }

    //get input args
    val Array(streamFilesPath, regionName, dynamoTableName) = args


    //load the data stream
    val dataStream = dataLoader("data")
    dataStream.printSchema()

    //split the input data into words
    val words = dataStream.as[String].flatMap(_.split("\\s+")).filter(s => !s.isEmpty)

    //calculate the word count
    words
      .groupBy("value")
      .count()
      .writeStream
      .outputMode("complete")
//      .format("console")
      .foreach(new DynamoDbWriter(dynamoTableName,regionName))
      .start()
      .awaitTermination()



  }
}
