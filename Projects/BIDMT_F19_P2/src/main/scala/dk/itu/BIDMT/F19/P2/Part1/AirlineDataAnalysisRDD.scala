package dk.itu.BIDMT.F19.P2.Part1

import com.typesafe.config.ConfigFactory
import org.apache.log4j.Level.{INFO,WARN}
import org.apache.log4j.LogManager
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object AirlineDataAnalysisRDD {

  val log = LogManager.getRootLogger
  log.setLevel(WARN)

  //create a spark context
  val conf: SparkConf = new SparkConf()
    .setAppName("AirlineDataAnalysisRDD")
    //run on 4 nodes --> have 4 partitioning files
    //.setMaster("local[4]") //comment before you create the jar file to be run on the cluster

  val sc: SparkContext = new SparkContext(conf)


  /**
    * Load the data from a cvs file and parse it into and RDD of FlightDelayCancellationInfo (RDD[FlightDelayCancellationInfo])
    * @param filePath
    * @return RDD of FlightDelayCancellationInfo objects
    */
  def dataLoader(filePath: String):RDD[FlightDelayCancellationInfo] = {
    val data: RDD[String] = sc.textFile(filePath)
    val header = data.first()
    val dataNoHeaders: RDD[String] = data.filter(row => row!=header)
    dataNoHeaders.map(data => FlightDelayCancellationInfo(data))
  }


  /**
    * Find a list of all airline identifiers (OP_CARRIER) that appears in the data
    *
    * @param airlinesData
    * @return list of distinct OP_CARRIER appearing in all airline delay and cancellation data:
    */
  def findDistinctAirlineCarriers(airlinesData: RDD[FlightDelayCancellationInfo]) : RDD[String] = {
    airlinesData.map(r => r.OP_CARRIER).distinct()
  }

  //Ranking Approach # 1 : count the cancellation entries that match each airline carrier

  /**
    * For a given carrier, count all the occurrences of cancellations in the input data RDD
    *
    * @param carrier
    * @param airlineCancellationsRDD
    * @return a count of all the occurrences of cancellations
    */
  def flightCancellationsForCarrier(carrier: String, airlineCancellationsRDD : RDD[FlightDelayCancellationInfo]) : Int = {
    //airlineCancellationsRDD.filter(x => x.OP_CARRIER == carrier).map(x => x.CANCELLED == "1.0").count().toInt
    //we have to filter after both. Somehow we mapped it only so that it just counted all my flights and not the cancelled only
    airlineCancellationsRDD.filter(x => x.OP_CARRIER == carrier && x.CANCELLED == "1.0").persist().count().toInt
  }

  /**
    * For each carrier in the input list, call flightCancellationsForCarrier to count all the occurrences of cancellations in the input data RDD
    * Sort the o/p in a descending order according to the number of cancellation occurrences
    *
    * @param airlineCancellationsRDD
    * @param carriers
    * @return an list of pairs (carrier code , number of cancellation occurrences)
    */

  def rankingByCounting(airlineCancellationsRDD : RDD[FlightDelayCancellationInfo] , carriers : List[String]) : List[(String, Int)] = {
    carriers.map((c: String) => (c, flightCancellationsForCarrier(c, airlineCancellationsRDD)))sortWith(_._2 > _._2)
  }



 //Ranking Approach # 2 : build an index that contains each carrier and the corresponding rows in the data
 //    that represent flight cancellations for that carrier,
 //then use that index to count the cancellation entries for each

 /**
   * Create an index that maps each carrier code to all rows in the data that correspond to it and that have
   * the column CANCELLED set to 1.0 (cancelled flights)
   *
   * @param airlineCancellationsRDD
   * @return an RDD of pairs (carrier code , an iterable of all the rows in the data corresponding to that carrier)
   */
 def generateIndexOfCancellations(airlineCancellationsRDD : RDD[FlightDelayCancellationInfo]) : RDD[(String,Iterable[FlightDelayCancellationInfo])] = {
   airlineCancellationsRDD.filter(x => x.CANCELLED == "1.0").persist().groupBy(x => x.OP_CARRIER)
 }

 /**
   * Given an index of carrier-data rows for each carrier, count the cancellation occurrencies for each carrier
   * Sort the o/p in a descending order according to the number of cancellation occurrences
   *
   * @param airlineCancellationsIndexRDD
   * @return an RDD of pairs (carrier code , number of cancellation occurrences)
   */
 def rankingUsingIndex(airlineCancellationsIndexRDD : RDD[(String,Iterable[FlightDelayCancellationInfo])]) : RDD[(String,Int)] = {
   airlineCancellationsIndexRDD.map(x => (x._1, x._2.size)).persist().sortBy(_._2, false)
 }

   //Ranking Approach # 3 : map each entry in the data to 0 or 1 based on whether a cancellation has occured or not,
   //then use reduceByKey to group these entries based on the carrier and count the cancellation occurrences for each carrier

   /**
     * Map each row in the input data into a pair containing the carrier code and value of 1 or 0 (1=cancellation, 0=no cancellation)
     * Then reduceByKey to group based on carrier and count the cancellation occurrences for each
     * Sort the o/p in a descending order according to the number of cancellation occurrences
     * @param airlineCancellationsRDD
     * @return an RDD of pairs (carrier code , number of cancellation occurrences)
     */
   def rankingByReduction(airlineCancellationsRDD : RDD[FlightDelayCancellationInfo]) : RDD[(String,Int)] = {
     airlineCancellationsRDD.map(r => (r.OP_CARRIER, if (r.CANCELLED == "1.0") 1 else 0)).persist().reduceByKey(_+_).sortBy(_._2, false)
   }



   //helping function to execute a specific approach for counting the data
   def rankAirlineCarriers(airlineDataRDD : RDD[FlightDelayCancellationInfo], outputfilePath: String, approach : Int) ={

     approach match{
       case 1 => {
         //rank the airline carriers using approach #1
         //First: find a list of distinct airlines in the dataset
         val distinctAirlines = findDistinctAirlineCarriers(airlineDataRDD).collect().toList
         //Second: use approach # 1 to find the count
         val rankedAirlineCarriersApproach1 = rankingByCounting(airlineDataRDD,distinctAirlines)
         //print the resulting ranking

         sc.parallelize(rankedAirlineCarriersApproach1).saveAsTextFile(outputfilePath+"_approach1")
       }
       case 2 =>{
         //rank the airline carriers using approach #2
         val airlineDataIndexedByCarriersWithCancellationRDD = generateIndexOfCancellations(airlineDataRDD)
         val rankedAirlineCarriersApproach2 = rankingUsingIndex(airlineDataIndexedByCarriersWithCancellationRDD)

         //print the resulting ranking
         rankedAirlineCarriersApproach2.saveAsTextFile(outputfilePath+"_approach2")
       }
       case 3 =>{
         //rank the airline carriers using approach #3
         val rankedAirlineCarriersApproach3 = rankingByReduction(airlineDataRDD)

         //print the resulting ranking
         rankedAirlineCarriersApproach3.saveAsTextFile(outputfilePath+"_approach3")
       }
       case _ => println("AirlineDataAnalysisRDD: invalid approach num, possible values are 1-3")
     }
   }

  def main(args: Array[String]): Unit = {

    //read the input and o/p file paths from config file
    val inputFilePath = ConfigFactory.load().getString("BIDMT.project2.data.airlines.delaysAndCancellationsInputFiles")
    val outputfilePath = ConfigFactory.load().getString("BIDMT.project2.data.airlines.outFilePath")

    //load input files
    val airlineDataRDD = dataLoader(inputFilePath)

    if(args.length < 1){
      //no command line argument to select the approach, then execute all three approaches
      rankAirlineCarriers(airlineDataRDD ,outputfilePath,1)
      rankAirlineCarriers(airlineDataRDD,outputfilePath,2)
      rankAirlineCarriers(airlineDataRDD,outputfilePath,3)
    }else{
      rankAirlineCarriers(airlineDataRDD,outputfilePath,args(0).toInt)
    }

    //stop spark
    sc.stop()
  }

}
