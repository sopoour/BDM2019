package dk.itu.BIDMT.Exercises.Exercise08

import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object BikeShareAnalysisSQL {

  val spark = SparkSession
    .builder
    .master("local")
    .appName("BikeShareAnaluysisSQL")
    .getOrCreate()


  def main(args: Array[String]): Unit = {

    import spark.implicits._

    //define myschema
    val mySchema =
      StructType(Array(
        StructField("Trip ID", IntegerType, true),
        StructField("Duration", IntegerType, true),
        StructField("Start Time", TimestampType, true),
        StructField("End Time", TimestampType, true),
        StructField("Starting Station ID", IntegerType, true),
        StructField("Starting Station Latitude", DoubleType, true),
        StructField("Starting Station Longitude", DoubleType, true),
        StructField("Ending Station ID", IntegerType, true),
        StructField("Ending Station Latitude", DoubleType, true),
        StructField("Ending Station Longitude", DoubleType, true),
        StructField("Bike ID", IntegerType, true),
        StructField("Plan Duration", IntegerType, true),
        StructField("Trip Route Category", StringType, true),
        StructField("Passholder Type", StringType, true),
        StructField("Starting Lat-Long", StringType, true),
        StructField("Ending Lat-Long", StringType, true)
      ))

    //load the data
    val bikeDataDF: DataFrame =
      spark
      .read
      .option("header","true")
      .schema(mySchema)
      //.option("inferschema","true")
      .csv("data/metro-bike-share-trips-2019-q1.csv")


    //showing first 10 tuples in the DataFrame
    //bikeDataDF.show(10)

    //printing the schema of the dataframe
    //bikeDataDF.printSchema()


    //showing different approaches to reference a column of a DataFrame
    bikeDataDF.persist()
    bikeDataDF.filter("Duration > 12*60").show(10)
    bikeDataDF.filter(bikeDataDF("Duration") > 12*60).show(10)
    bikeDataDF.filter($"Duration" > 12*20).show(10)


    //Q1: selecting all tuples from a dataframe

    bikeDataDF.createTempView("BikeShareData")
    val allBikeDataSQL: DataFrame = spark.sql("select * from BikeShareData")
    allBikeDataSQL.show(5)

    val allBikeDataDF = bikeDataDF.select("*")
    allBikeDataDF.show(5)



    //Q2: selecting all tuples from dataframe, projecting on columns `Trip ID`,`Bike ID`

    bikeDataDF.createTempView("BikeShareData")
    spark.sql("select `Trip ID`,`Bike ID` from BikeShareData").show(5)

    bikeDataDF.select("Trip ID","Bike ID").show(5)


    //Q3: selecting tuples from dataframe where duration of the trip exceeds 12 hrs, projecting on columns `Trip ID`,`Duration`

    bikeDataDF
      .select("Trip ID","Duration")
      .filter($"Duration" > 12*60)
      .show(5)

    bikeDataDF.createTempView("BikeDataView")
    spark.sql("""
      select `Trip ID`,`Duration`
      from BikeDataView
      where Duration > 12*60
              """).show(5)

    //Q4 same as query 3, except that we order the result in a descending order

    bikeDataDF
      .select("Trip ID","Duration")
      .filter($"Duration" > 12*60)
      .orderBy(desc("Duration"))
      .show(5)

    bikeDataDF.createTempView("BikeDataView")
    spark.sql("""
      select `Trip ID`,`Duration`
      from BikeDataView
      where Duration > 12*60
      order by Duration DESC
              """).show(5)


    //Q5: counting all the trips made by each bike
    bikeDataDF
        .filter($"Duration" > 12*60)
        .groupBy("Bike ID")
        .agg(count("*"))
        .withColumnRenamed("count(1)","tripCounts")
        //.select("Bike ID", "count(1)")
        .show(5)

    bikeDataDF.createTempView("BikeDataView")
    spark.sql("""
                 select `Bike ID`, count(*) as tripCounts
                 from BikeDataView
                 where Duration > 12*60
                 group by `Bike ID`
              """).show(5)


    //Q6: The average duration made by each bike, order the result in a descending order according to the avg duration
    spark.sql(
      """
        |select `Starting Station ID`, avg(Duration) as avgDuration
        |from BikeShareData
        |group by `Starting Station ID`
        |order by avgDuration DESC
        |""".stripMargin).show(5)

    bikeDataDF.groupBy("Starting Station ID")
      .avg("Duration")
      .withColumnRenamed("avg(Duration)","avgDuration")
      .orderBy(desc("avgDuration"))
      .select("Starting Station ID","avgDuration")
      .show(5)


    spark.close()
  }
}
