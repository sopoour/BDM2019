package dk.itu.BIDMT.Exercises.Exercise12

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.typesafe.config.ConfigFactory
import org.apache.log4j.LogManager
import org.apache.log4j.Level._
import org.apache.spark.sql.{Dataset, Encoders, SparkSession, DataFrame, Row}

import org.apache.spark.ml.clustering.{KMeans, KMeansModel}
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.linalg
import org.apache.spark.sql.functions.struct
import org.apache.spark.sql.functions.array
import org.apache.spark.sql.functions.lit

object MeteoriteCluster {

    
  org.apache.log4j.Logger getLogger "org" setLevel (org.apache.log4j.Level.OFF)
  org.apache.log4j.Logger getLogger "akka" setLevel (org.apache.log4j.Level.OFF)


  //val log = LogManager.getRootLogger
  //log.setLevel(INFO)

	val spark: SparkSession = SparkSession.builder
        .appName("MyApp")
        .master("local") //Remove this line when running in cluster
        .getOrCreate

	spark.sparkContext.setLogLevel("OFF")
	import spark.implicits._


	def LoadDataSet ( path:String):DataFrame =  {
		spark
			.read
      .format("csv")
			.option("header","true")
      .option("inferSchema", "true")
      .load(path)
      .toDF
  }

  /**
    Return KMeansModel that has been fitted to the dataset. 
    The KMeansModel has k clusters and is seeded with seed. 
  */
  def getKmeans(ds:Dataset[Row], k:Int, seed:Long):KMeansModel = {
    // Get the coordinates from dataset
    ???

    //  Create K-Means model

    // Fit K-Means model to coordinates

    // Return K-Means model
  }

  def main(args: Array[String]): Unit = {

    val input_file =    ConfigFactory.load().getString("data.inputfilepath")
    val output_folder = ConfigFactory.load().getString("data.outputfilepath")

    //load data
    val ds = LoadDataSet(input_file)

    val meteoriteKMeans = getKmeans(ds, 3, 1L)

    meteoriteKMeans
      .clusterCenters
      .foreach(_.toArray.foreach(println))

    spark.stop

  }
}
