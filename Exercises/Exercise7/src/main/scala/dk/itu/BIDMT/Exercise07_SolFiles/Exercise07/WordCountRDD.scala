package dk.itu.BIDMT.Exercises.Exercise07

import org.apache.spark.{SparkConf, SparkContext}

object WordCountRDD {


  val conf = new SparkConf()
    .setAppName("wordCount")
    .setMaster("local[2]")
  val sc = new SparkContext(conf)


  def main(args: Array[String]): Unit = {
    //load the data

    val fileText = sc.textFile("data/pg1597.txt")

    //split data into words
    val words = fileText.flatMap(x => x.split("\\s+"))

    //first approach to perform word count
    val wordCountRDD_1 = words
      .map(x => (x,1))
      .reduceByKey(_+_)

    //save o/p to file
    wordCountRDD_1.saveAsTextFile("out/wordCountApproach1")

    //sorting the o/p of word count in descending order
    val wordCountRDD_1_Sorted = wordCountRDD_1.sortBy(_._2,false)
    wordCountRDD_1_Sorted.saveAsTextFile("out/wordCountApproach1_sortedDesc")

    //second approach to perform word count
    val wordCountRDD_2 = words
      .groupBy(x => x)
      .map(r => (r._1, r._2.size))

    //save o/p to file
    wordCountRDD_2.saveAsTextFile("out/wordCountApproach2")


    //sorting the o/p of word count in ascending order
    val wordCountRDD_2_Sorted = wordCountRDD_2.sortBy(_._2)
    wordCountRDD_2_Sorted.saveAsTextFile("out/wordCountApproach2_sortedAsc")

  }
}
