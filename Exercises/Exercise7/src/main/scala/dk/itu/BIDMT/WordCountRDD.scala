package dk.itu.BIDMT.ExerciseMain

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

// you are intended to run this LOCALLY (not using your AWS cluster!) 
// using the SBT console ('sbt console' in root of project directory)

/// You can access declared variables, functions, etc. by writing the name of the class
/// i.e: 'WordCountRDD.name-of-var/function'

object WordCountRDD {
    //Create a Spark Context
    val conf = new SparkConf().setMaster("local").setAppName("Word Count")
    val sc = new SparkContext(conf)

    def main(args: Array[String]): Unit = {
      //load the data
      val inputFilePath = "data/pg1597.txt"
      val inputRDD = sc.textFile(inputFilePath)

      // split the text read from file into words
      val wordsRDD = inputRDD.flatMap(line => line.split(" "))

      //split data into words, transform into word and count and reduce
      //flatMap: create an array of separated words
      //map: makes tuples of words and starts with 1
      //reduceByKey: CHECK what it does! --> counter probably
      val countsRDD = wordsRDD.map(word =>(word,1)).reduceByKey(_+_)

      //write an action here to start executing your code
      countsRDD.saveAsTextFile("data/output")

      //stop spark
      sc.stop()
    }
  /*
    //load the data
      val fileText = sc.textFile(path="data/pg1597")

      //split data into words
      val words = fileText.flatMap(x => x.split("\\s+"))
      //first possibility
      val out1 = words.map(x=> (x,1)).reduceByKey(_+_)

      //second possibility
      //group by elements what we have in input RDD
      val out2 = words.groupBy(x=>x).map(r => (r._1, r._2.size))*/
}
