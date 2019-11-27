package dk.itu.BIDMT.Exercises.Exercise13

import java.io.BufferedOutputStream

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}

import scala.util.Random


object WordStreamProducer {

  val spark = SparkSession
    .builder()
    .appName("StreamWordCountApp")
    //.master("local")
    .getOrCreate()

  val randomWords = List("apache" , "spark", "streaming", "SQL", "ML", "kinesis", "aws")

  def generateBatches(streamOutFilesPath: String,
                      numBatches: Int,
                      recordsPerBatch: Int,
                      wordsPerRecord: Int,
                      waitBetweenBatch: Int): Seq[(String, Int)] = {

    //get the hdfs information from spark context
    import org.apache.hadoop.fs.{FileSystem,Path}
    val hdfs = FileSystem.get(spark.sparkContext.hadoopConfiguration)

    val totalWords = scala.collection.mutable.Map[String, Int]()

    for(batchNum <- 1 to numBatches){
      //for each batch
      //create outFile in HDFS given the file name
      val outFile = hdfs.create(new Path(streamOutFilesPath+"/batch_"+batchNum))
      //create a BufferedOutputStream to write to the file
      val out = new BufferedOutputStream(outFile)

      //for each record
      for(recordNum <- 1 to recordsPerBatch){
        // Randomly generate wordsPerRecord number of words
        val data = (1 to wordsPerRecord.toInt).map(x => {
          // Get a random index to a word
          val randomWordIdx = Random.nextInt(randomWords.size)
          val randomWord = randomWords(randomWordIdx)

          // Increment total count to compare to server counts later
          totalWords(randomWord) = totalWords.getOrElse(randomWord, 0) + 1

          randomWord
        }).mkString(" ")

        //write generated record to batch
        out.write(data.getBytes)
        out.write('\n')
      }


      //close file
      out.close()

      //wait
      Thread.sleep(waitBetweenBatch)
    }

    // Convert the totals to (index, total) tuple
    totalWords.toSeq.sortBy(_._1)

  }

  def main(args: Array[String]): Unit = {

    if(args.length < 5){
      println("Usage: WordStreamProducer streamOutPath numBatches recordsPerBatch wordsPerRecord waitBetweenBatch")
      System.exit(1)
    }
    //get input args
    val Array(streamOutFilesPath, numBatches, recordsPerBatch, wordsPerRecord, waitBetweenBatch) = args

    //generate batches
    val totalWords = generateBatches(streamOutFilesPath, numBatches.toInt, recordsPerBatch.toInt, wordsPerRecord.toInt, waitBetweenBatch.toInt)

    // Print the array of (word, count) tuples
    println("Total number of words written to all batches")
    totalWords.foreach(println(_))
  }

}
