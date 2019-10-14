package dk.itu.BIDMT.ExerciseMain
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

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

  def main(args: Array[String]): Unit = {
    //Note: make sure that you download the dataset to the below location before you run your application
    val inputFilePath = "data/metro-bike-share-trips-2019-q1.csv"
    //load the data
    val bikeShareDataRDD = dataLoader(inputFilePath)

    //app1: find trips whose duration is greater than 12 hr

    //filter RDD based on a given predicate/condition (see above comment)
    val longTrips = bikeShareDataRDD.filter(r => r.duration >= 12*60)

    //sort in an ascending order based on duration
    val longTripsSortedAsc = longTrips.sortBy(r => r.duration)

    //sort in descending order based on duration
    val longTripsSortedDsc = longTrips.sortBy(r => r.duration, false)

    //app2: projecting on trip_id, duration

    //selecting on specific columns (here: trip id and duration)
    val tripAndDurationOnly = bikeShareDataRDD.map(r => (r.trip_id, r.duration))

    //app3: find number of trips made by each bike

    //group RDDs based on bike_id then map/count on how often that ID appears (r._2.size), then sort in descending order
    val bikeTripsDsc = bikeShareDataRDD.groupBy(r => r.bike_id).map(r => (r._1, r._2.size)).sortBy(_._2, false)

    //app4: find total duration of trips made by each bike

    //map RDDs with bike_id and duration then finally reduce all of that to one tuple with the sum of duration for each ID
    //val bikeDurationUsed = bikeShareDataRDD.map(r => (r.bike_id, r.duration)).reduceByKey(_+_)

    bikeTripsDsc.saveAsTextFile("data/output-bike")

    sc.stop()
  }
}
