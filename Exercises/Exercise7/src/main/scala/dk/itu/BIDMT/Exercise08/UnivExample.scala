package dk.itu.BIDMT.Exercises.Exercise08

import org.apache.spark.{SparkConf, SparkContext}


case class Faculty(fID: Int,
                   name: String,
                   office: String,
                   position: String)

object Faculty{
  def apply(facultyData : String): Faculty = {
    val facultyDataSplitted = facultyData.split(",")
    new Faculty(facultyDataSplitted(0).toInt, facultyDataSplitted(1), facultyDataSplitted(2), facultyDataSplitted(3))
  }
}
case class Programs(progID: String,
                    name: String,
                    budget: String)
object Programs{
  def apply(programData : String): Programs = {
    val programDataSplitted = programData.split(",")
    new Programs(programDataSplitted(0), programDataSplitted(1), programDataSplitted(2))
  }
}

case class WorksIn(fID: Int,
                   progID: String,
                   since: String)

object WorksIn{
  def apply(WorksInData : String): WorksIn = {
    val WorksInDataSplitted = WorksInData.split(",")
    new WorksIn(WorksInDataSplitted(0).toInt, WorksInDataSplitted(1), WorksInDataSplitted(2))
  }
}

object UnivExample {

  val conf: SparkConf = new SparkConf()
    .setAppName("FacultyMembersAnalysis")
    .setMaster("local")
  val sc: SparkContext = new SparkContext(conf)


  def main(args: Array[String]): Unit = {
    //load the data
    val facultyRDD = sc.textFile("data/faculty.csv")
      .map(data => Faculty(data))
    val programRDD = sc.textFile("data/programs.csv")
      .map(data => Programs(data))
    val worksinRDD = sc.textFile("data/worksin.csv")
      .map(data => WorksIn(data))


    //join approach # 1
    facultyRDD.cartesian(worksinRDD)
      .filter(t => t._1.fID == t._2.fID)
      .filter(t => t._2.progID == "SD")
      .map(t => t._1.name)
      .collect()
      .foreach(println)

    //join approach # 2
    val worksinSDRDD = worksinRDD
      .filter(t => t.progID == "SD")

    facultyRDD.cartesian(worksinSDRDD)
      .filter(t => t._1.fID == t._2.fID)
      .map(t => t._1.name)
      .collect()
      .foreach(println)

    //join approach # 3
    val worksinDSRDD = worksinRDD
      .filter(t => t.progID == "SD")

    val facultyProjectedRDD = facultyRDD
      .map(t => (t.fID, t.name))

    facultyProjectedRDD
      .cartesian(worksinDSRDD)
      .filter(t => t._1._1 == t._2.fID)
      .map(t => t._1._2)
      .collect()
      .foreach(println)
  }

}
