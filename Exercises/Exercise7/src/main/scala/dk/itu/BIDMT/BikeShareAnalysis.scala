package dk.itu.BIDMT.ExerciseMain
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

// you are intended to run this LOCALLY (not using your AWS cluster!) 
// using the SBT console ('sbt console' in root of project directory)

/// You can access declared variables, functions, etc. by writing the name of the class
/// i.e: 'BikeShareAnalysis.name-of-var/function'

object BikeShareAnalysis {
  val conf = new SparkConf().setMaster("local").setAppName("Word Count")
  val sc = new SparkContext(conf)

  val dataRDD = sc.textFile("data/metro-bike-share-trips-2019-q1.csv")

  val header = dataRDD.first()

  val dataRDDNoHeader = dataRDD.filter(row => row != header)

  val  bikeShareDataRDD = dataRDDNoHeader.map(row => BikeShareData(row))
  
  //app1: find trips whose duration is greater than 12 hr
  val longTrips = bikeShareDataRDD...

  val longTripsSortedAsc = longTrips...

  val longTripsSortedDsc = longTrips...

  //app2: projecting on trip_id, duration
  val tripAndDurationOnly = bikeShareDataRDD...

  //app3: find number of trips made by each bike
  val bikeTripsDsc = bikeShareDataRDD...

  //app4: find total duration of trips made by each bike
  val bikeDurationUsed = bikeShareDataRDD...
}
