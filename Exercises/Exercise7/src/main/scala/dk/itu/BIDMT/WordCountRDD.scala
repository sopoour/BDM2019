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
        val fileText = sc.textFile(path="data/pg1597")

        //split data into words
        val words = fileText.flatMap(x => x.split("\\s+"))
        //first possibility
        val out1 = words.map(x=> (x,1)).reduceByKey(_+_)

        //second possibility
        //group by elements what we have in input RDD
        val out2: RDD[(String, Iterable[String])]= words.groupBy(x=>x).map(r => (r._1, r._2.size))
    }
    val inputRDD = sc.textFile("/home/userid/input/*")
    //split data into words, transform into word and count and reduce
    //flatMap: create an array of separated words
    //map: makes tuples of words and starts with 1
    //reduceByKey: CHECK what it does! --> counter probably
    val countsRDD = inputRDD.flatMap(line => line.split(" "))
      .map(word =>(word,1))
      .reduceByKey(_+_)

    countsRDD.saveAsTextFile("/home/userid/output")
}
