package dk.itu.BIDMT.ExerciseMain
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

// you are intended to run this LOCALLY (not using your AWS cluster!) 
// using the SBT console ('sbt console' in root of project directory)

/// You can access declared variables, functions, etc. by writing the name of the class
/// i.e: 'WordCountRDD.name-of-var/function'

object WordCountRDD {
    val conf = new SparkConf().setMaster("local").setAppName("Word Count")
    val sc = new SparkContext(conf)

    val inputRDD = sc...

    val countsRDD = inputRDD...

    countsRDD...
}
