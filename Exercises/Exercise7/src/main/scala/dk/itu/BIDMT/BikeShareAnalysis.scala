package dk.itu.BIDMT.ExerciseMain
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import com.typesafe.config.ConfigFactory

// you are intended to run this LOCALLY (not using your AWS cluster!) 
// using the SBT console ('sbt console' in root of project directory)

/// You can access declared variables, functions, etc. by writing the name of the class
/// i.e: 'BikeShareAnalysis.name-of-var/function'

object BikeShareAnalysis {
  val conf = new SparkConf().setMaster("local").setAppName("Word Count")
  val sc = new SparkContext(conf)

  def dataLoader(filePath: String): RDD[BikeShareData] = {
    val dataRDD = sc.textFile("data/metro-bike-share-trips-2019-q1.csv")

    val header = dataRDD.first()

    val dataRDDNoHeader = dataRDD.filter(row => row != header)

    val  bikeShareDataRDD = dataRDDNoHeader.map(row => BikeShareData(row))
    bikeShareDataRDD
  }

  //app1: find trips whose duration is greater than 24 hr
  def findLongTrips(bikeShareDataRDD: RDD[BikeShareData]):RDD[BikeShareData] = {
    bikeShareDataRDD.filter(r => r.duration >= 12*60)
  }

  //app2: find number of trips made by each bike
  //group RDDs based on bike_id then map/count on how often that ID appears (r._2.size), then sort in descending order
  def findNumTripsPerBike(bikeShareDataRDD: RDD[BikeShareData]):RDD[(String,Int)] ={
    bikeShareDataRDD.groupBy(_.bike_id).map(r => (r._1, r._2.size)).sortBy(_._2,false)
  }

  //app3: projecting on trip_id, duration
  //selecting on specific columns (here: trip id and duration)
  def findTripDurations(bikeShareDataRDD: RDD[BikeShareData]):RDD[(Int,Int)] ={
    bikeShareDataRDD.map(r => (r.trip_id,r.duration))
  }

  //app4
  //map RDDs with bike_id and duration then finally reduce all of that to one tuple with the sum of duration for each ID
  def findTotalDurationPerBike(bikeShareDataRDD: RDD[BikeShareData]):RDD[(String,Int)] ={
    bikeShareDataRDD.map(r => (r.bike_id , r.duration)).reduceByKey(_+_)
  }

  def main(args: Array[String]): Unit = {
    //Note: make sure that you download the dataset to the below location before you run your application
    val configFile = ConfigFactory.load()
    val inputFilePath = "data/metro-bike-share-trips-2019-q1.csv" // configFile.getString("exercise07.BikeShareData.inputFile")
    //load the data
    val bikeShareDataRDD = dataLoader(inputFilePath)
    bikeShareDataRDD.take(10).map(println)

    //app1: find trips whose duration is greater than 24 hr
    //sort in ascending order based on duration
    val longTripsRDDSortedAsc = findLongTrips(bikeShareDataRDD).sortBy(r => r.duration)
    longTripsRDDSortedAsc.take(5).foreach(println)

    //sort in descending order based on duration
    val longTripsSortedDsc = findLongTrips(bikeShareDataRDD).sortBy(r => r.duration, false)
    longTripsSortedDsc.take(5).foreach(println)

    //app2: find number of trips made by each bike
    val numTripsPerBikeRDD = findNumTripsPerBike(bikeShareDataRDD)
    numTripsPerBikeRDD.take(5).foreach(println)

    //app3: projecting on trip_id, duration
    val tripDurationRDD = findTripDurations(bikeShareDataRDD)
    tripDurationRDD.take(5).foreach(println)

    //app4: find total duration of trips made by each bike
    val durationTripsPerBikeRDD = findTotalDurationPerBike(bikeShareDataRDD)
    durationTripsPerBikeRDD.take(5).foreach(println)

    sc.stop()
  }
}
