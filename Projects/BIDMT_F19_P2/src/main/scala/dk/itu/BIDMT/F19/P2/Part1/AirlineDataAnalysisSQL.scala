package dk.itu.BIDMT.F19.P2.Part1

import org.apache.spark.sql.{DataFrame, SparkSession}
import com.typesafe.config.ConfigFactory

object AirlineDataAnalysisSQL {
  val spark = SparkSession
    .builder()
    .appName("AirlineDataAnalysisSQL")
    //.master("local[4]") //comment before you create the jar file to be run on the cluster
    .getOrCreate()

  spark.sparkContext.setLogLevel("WARN")

  //load the data
  def dataLoader(path: String):DataFrame ={
    spark.read.format("csv").option("header", "true").load(path)
  }

  def implForAirlineDataAnalysisSQL(inputFilePath: String, outputfilePath: String) = {
    //load the data
    val delaysAndCancellations = dataLoader(inputFilePath)
    delaysAndCancellations.persist()

    //rank the airline carriers based on the cancellation occurrences per carrier
    def airlineRankingSQLCancelled(): DataFrame = {
      spark.sql(
        """
                   SELECT OP_CARRIER, COUNT(CANCELLED)
                   FROM delaysAndCancellationsView
                   GROUP BY OP_CARRIER, CANCELLED
                   HAVING CANCELLED = 1.0
            """)
    }
    // , write the o/p to a file : outputfilePath+"_cancellation" (outputfilePath is read from configuration file)
    delaysAndCancellations.createTempView("delaysAndCancellationsView")
    val cancellationOccurrencesSQL = airlineRankingSQLCancelled()
    cancellationOccurrencesSQL.write.mode("overwrite").csv(outputfilePath+"_cancellation")

  //rank the airline carriers based on the total delay times over a year
  def airlineRankingSQLDelays() : DataFrame = {
    spark.sql("""
                 SELECT YEAR(FL_DATE) as year, OP_CARRIER, SUM(ARR_DELAY)
                 FROM DelayRankingView
                 WHERE ARR_DELAY > 0
                 GROUP BY year, OP_CARRIER
          """)
  }
    // write the o/p to a file : outputfilePath+"_delays" (outputfilePath is read from configuration file)
    delaysAndCancellations.createTempView("DelayRankingView")
    val delayOccurrencesSQL = airlineRankingSQLDelays()
    delayOccurrencesSQL.write.mode("overwrite").csv(outputfilePath+"_delays")
  }

    def main(args: Array[String]): Unit = {

      val airlinesDelaysAndCancellationsFilePath = ConfigFactory.load().getString("BIDMT.project2.data.airlines.delaysAndCancellationsInputFiles")
      val AirlineDataAnalysisSQLOutFilePath = ConfigFactory.load().getString("BIDMT.project2.data.airlines.outFilePathSSQL")

      implForAirlineDataAnalysisSQL(airlinesDelaysAndCancellationsFilePath, AirlineDataAnalysisSQLOutFilePath)

      spark.close()
      spark.stop()
    }




}
