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

    

  def main(args: Array[String]): Unit = {
    //make sure that you download the dataset first (also available from: http://www.gutenberg.org/ebooks/1597)
    val inputFilePath = "data/pg1597.txt"
    val inputRDD = ??? //sc...

    // split the text read from file into words
    val wordsRDD = ??? 

    //write code to calculate word count
    val countsRDD = ??? //wordRDD...

    //write an action here to start executing your code
    //countRDD... 

    //stop spark
    sc.stop()
  }
}
