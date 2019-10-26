package dk.itu.BIDMT.Exercises.Exercise07

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object TestRDD {

  val conf = new SparkConf().setAppName("test").setMaster("local")
  val sc = new SparkContext(conf)


  def test00() = {

    val list = List(1,2,3,4,5)
    val rdd: RDD[Int] = sc.parallelize(list)

    val rddGT2 = rdd.filter(x => x>2)

    //rddGT2.collect().foreach(println)

    //rddGT2.top(2)foreach(println)

    println("sum of elements"+ rddGT2.reduce((x,y) => x+y))
  }


  def test01() ={
    val rdd: RDD[String] = sc.textFile("data/textFile.txt")
    //rdd.collect().foreach(println)

    val words: RDD[String] = rdd.flatMap(x => x.split(" "))
    words.collect.foreach(println)
  }

  def test02() ={

    val r00 = sc.parallelize(0 to 9)
    val r01 = sc.parallelize(0 to 90 by 10)

    /*r00.collect().foreach(println)
    r01.collect().foreach(println)*/

    val r10 = r00.cartesian(r01)
    //r10.collect().foreach(println)
    val r11 = r00.map(x => (x,x))
    val r12 = r00.zip(r01)
    val r13 = r01.keyBy(x => x/20)

    val r20 = r10.union(r11).union(r12).union(r13)
    val r21 = Seq(r11,r12,r13).foldLeft(r10)(_ union _)
    r21.collect().foreach(println)
  }
  def main(args: Array[String]): Unit = {
    //test00()
    //test01()
    test02()
  }
}
