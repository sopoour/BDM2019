package dk.itu.BIDMT.ExerciseMain

//import org.apache.spark.sql.{Dataset, Encoders, SparkSession, DataFrame, Row}
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.types._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel._
object Main {
    
  	org.apache.log4j.Logger getLogger "org" setLevel (org.apache.log4j.Level.OFF)
  	org.apache.log4j.Logger getLogger "akka" setLevel (org.apache.log4j.Level.OFF)

    val conf = new SparkConf()
        .setMaster("local")
        .setAppName("main")
    val sc = new SparkContext(conf)

	//spark.sparkContext.setLogLevel("OFF")
    //import spark.implicits._

	//def simulatedDataLoader (spark:SparkSession, path:String):RDD[Row] =  {
	//	spark
	//		.read
    //        .format("csv")
	//		.option("header","true")
    //        .option("inferSchema", "true")
    //        .load(path)
	//}

	def main(args:Array[String]): Unit = {
        val dataRDD = sc.textFile("data/medium_dataset.csv")
        val header = dataRDD.first()
        val dataRDDNoHeader = dataRDD.filter(row => row != header)

        // Print the first 20 elements in the RDD
        //dataRDDNoHeader.take(20).map(println)

        // Count the number of elements
        val count = dataRDDNoHeader...

        // Filter based on X and count
        val firstContains = dataRDDNoHeader...

        val secondContains =  dataRDDNoHeader...

        // Find the ID that is most frequent (should result in 41)
        val mostFrequentId = dataRDDNoHeader...

        // Try to add .persist() on reused data

    }
}
