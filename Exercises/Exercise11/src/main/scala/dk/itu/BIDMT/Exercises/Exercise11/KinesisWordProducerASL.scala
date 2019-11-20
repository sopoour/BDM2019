//based  on the code example in https://github.com/apache/spark/blob/master/external/kinesis-asl/src/main/scala/org/apache/spark/examples/streaming/KinesisWordCountASL.scala

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package dk.itu.BIDMT.Exercises.Exercise11

import java.nio.ByteBuffer

import com.amazonaws.auth.{AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.kinesis.{AmazonKinesisClient, AmazonKinesisClientBuilder}
import com.amazonaws.services.kinesis.model.PutRecordRequest

import scala.util.Random


/**
  * Usage: KinesisWordProducerASL <stream-name> <endpoint-url> \
  *   <records-per-sec> <words-per-record>
  *
  *   <stream-name> is the name of the Kinesis stream (ie. mySparkStream)
  *   <endpoint-url> is the endpoint of the Kinesis service
  *     (ie. https://kinesis.us-east-1.amazonaws.com)
  *   <records-per-sec> is the rate of records per second to put onto the stream
  *   <words-per-record> is the rate of records per second to put onto the stream
  *
  * Example:
  *    $ SPARK_HOME/bin/run-example streaming.KinesisWordProducerASL mySparkStream \
  *         https://kinesis.us-east-1.amazonaws.com us-east-1 10 5
  */
object KinesisWordProducerASL {

  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      System.err.println(
        """
          |Usage: KinesisWordProducerASL <stream-name> <endpoint-url> <records-per-sec>
                                         <words-per-record>
          |
          |    <stream-name> is the name of the Kinesis stream
          |    <endpoint-url> is the endpoint of the Kinesis service
          |                   (e.g. https://kinesis.us-east-1.amazonaws.com)
          |    <records-per-sec> is the rate of records per second to put onto the stream
          |    <words-per-record> is the rate of records per second to put onto the stream
          |
        """.stripMargin)

      System.exit(1)
    }

    // Set default log4j logging level to WARN to hide Spark logs
    StreamingExamples.setStreamingLogLevels()

    // Populate the appropriate variables from the given args
    val Array(stream, endpoint, recordsPerSecond, wordsPerRecord) = args

    // Generate the records and return the totals
    val totals = generate(stream, endpoint, recordsPerSecond.toInt,
      wordsPerRecord.toInt)

    // Print the array of (word, total) tuples
    println("Totals for the words sent")
    totals.foreach(println(_))
  }

  def generate(stream: String,
               endpoint: String,
               recordsPerSecond: Int,
               wordsPerRecord: Int): Seq[(String, Int)] = {

    val randomWords = List("spark", "you", "are", "my", "father")
    val totals = scala.collection.mutable.Map[String, Int]()

    // Get the region name from the endpoint URL to save Kinesis Client Library metadata in
    // DynamoDB of the same region as the Kinesis stream
    val regionName = KinesisExampleUtils.getRegionNameByEndpoint(endpoint)

    // attempts to retrieve the aws credentials on the executing system.
    val credentials = new DefaultAWSCredentialsProviderChain().getCredentials()
    require(credentials != null,
      "No AWS credentials found. Please specify credentials using one of the methods specified " +
        "in http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html")

    // Create the low-level Kinesis Client from the AWS Java SDK.
    val kinesisClientBuilder =  AmazonKinesisClientBuilder.standard()
    kinesisClientBuilder.withCredentials(new DefaultAWSCredentialsProviderChain())
    kinesisClientBuilder.setEndpointConfiguration(new EndpointConfiguration(endpoint,regionName))
    val kinesisClient= kinesisClientBuilder.build()

    println(s"Putting records onto stream $stream and endpoint $endpoint at a rate of" +
      s" $recordsPerSecond records per second and $wordsPerRecord words per record")

    // Iterate and put records onto the stream per the given recordPerSec and wordsPerRecord
    for (i <- 1 to 10) {
      // Generate recordsPerSec records to put onto the stream
      val records = (1 to recordsPerSecond.toInt).foreach { recordNum =>
        // Randomly generate wordsPerRecord number of words
        val data = (1 to wordsPerRecord.toInt).map(x => {
          // Get a random index to a word
          val randomWordIdx = Random.nextInt(randomWords.size)
          val randomWord = randomWords(randomWordIdx)

          // Increment total count to compare to server counts later
          totals(randomWord) = totals.getOrElse(randomWord, 0) + 1

          randomWord
        }).mkString(" ")

        // Create a partitionKey based on recordNum
        val partitionKey = s"partitionKey-$recordNum"

        // Create a PutRecordRequest with an Array[Byte] version of the data
        val putRecordRequest = new PutRecordRequest().withStreamName(stream)
          .withPartitionKey(partitionKey)
          .withData(ByteBuffer.wrap(data.getBytes()))

        // Put the record onto the stream and capture the PutRecordResult
        val putRecordResult = kinesisClient.putRecord(putRecordRequest)
      }

      // Sleep for a second
      Thread.sleep(1000)
      println("Sent " + recordsPerSecond + " records")
    }
    // Convert the totals to (index, total) tuple
    totals.toSeq.sortBy(_._1)
  }
}
