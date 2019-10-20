package dk.itu.BIDMT.ExerciseMain

import org.apache.spark.{SparkConf, SparkContext}
object Main {
    
  	org.apache.log4j.Logger getLogger "org" setLevel (org.apache.log4j.Level.OFF)
  	org.apache.log4j.Logger getLogger "akka" setLevel (org.apache.log4j.Level.OFF)

    val conf = new SparkConf()
        .setMaster("local")
        .setAppName("main")
    val sc = new SparkContext(conf)

	def main(args:Array[String]): Unit = {
    val dataRDD = sc.textFile("data/medium_dataset.csv")
    val header = dataRDD.first()
    val dataRDDNoHeader = dataRDD.filter(row => row != header)
    println(header)

    // Print the first 20 elements in the RDD
    dataRDDNoHeader.take(20).map(println)
      //also possible to write: dataRDDNoHeader.take(20).foreach(println)

    // Count the number of elements
    val count = dataRDDNoHeader.count()
    println("Number of elements: " + count)

    // Filter based on X and count
    val first = dataRDDNoHeader.map(_.split(", ")).map(c => c(1))
    val firstContains = first.filter(s => s.contains("X")).count()
    println("First counts X: " + firstContains)

    val second = dataRDDNoHeader.map(_.split(", ")).map(c => c(2))
    val secondContains = second.filter(s => s.contains("X")).count()
    println("Second counts X: " + secondContains)

    // Find the ID that is most frequent (should result in 41)
    //val mostFrequentId = dataRDDNoHeader....

        // Try to add .persist() on reused data

    }
}
