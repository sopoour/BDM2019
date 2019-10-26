package dk.itu.BIDMT.Exercises.Exercise08

import org.apache.spark.sql.SparkSession

object UnivExampleSQL {

  val spark =
    SparkSession
    .builder()
    .appName("UnivEx")
    .master("local")
    .getOrCreate()


  def main(args: Array[String]): Unit = {

    val facultyDF =
      spark
      .read
      .option("inferschema","true")
      .csv("data/faculty.csv")//.show()

    val worksinDF =
      spark
        .read
        .option("inferschema","true")
        .csv("data/worksin.csv")//.show()


    //Query: Find faculty members who teach in the “Software Design Program”

    //SQL solution

    facultyDF.createTempView("facultyView")
    worksinDF.createTempView("worksinView")

    spark.sql(
      """
        |select f._c1
        |from facultyView f , worksinView w
        |where  f._c0 = w._c0 and w._c1 = "SD"
      """.stripMargin).show()


    //DF transformation solution
    facultyDF
      .join(worksinDF, facultyDF("_c0") === worksinDF("_c0"),"inner")
      .where(worksinDF("_c1") === "SD")
      .select(facultyDF("_c1"))
      .show()


    spark.close()
  }

}
