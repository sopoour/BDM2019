package dk.itu.BIDMT.F19.P3

import java.io.{BufferedOutputStream, PrintWriter}
import scala.collection.mutable.ArrayBuffer
import com.typesafe.config.ConfigFactory
import org.apache.spark.ml.clustering.{KMeans, KMeansModel}
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

import scala.collection.mutable.ListBuffer

object AmazonProductsClustering {

  //create a spark session
  val spark = SparkSession
    .builder()
    .appName("AmazonProductsClustering")
    .master("local[4]")
    .getOrCreate()

  //load spark implicits
  import spark.implicits._


  /*Load the data stored as a json file given the file path*/
  def jsonDataLoader(filePath : String):DataFrame = {
    spark.read.json(filePath)
  }

   /**
     * Given a DataFrame of the products metadata and an integer k, find the top reocurring k categories
     *
     * NOTE: you will need to use the function explode described in: https://spark.apache.org/docs/latest/api/java/org/apache/spark/sql/functions.html
     *
     * The steps you need to follow:
     * 1- First, you need to use the function explode twice to generate a row for each category in the "categories" column
     * 2- Count the number of products tagged by each category, sort all categories according to that count, and return the top k of them
     * 3- Finaly, convert the result from the previous step into an Array[String]
     *
     * @param productsMetadataDF
     * @param k
     * @return An array containing the top k categories found in the dataset
     */
 def findTopKCategory(productsMetadataDF : DataFrame, k:Int) : Array[String] = {
   productsMetadataDF
     .withColumn("categories", explode($"categories"))
     .withColumn("categories", explode($"categories"))
     .groupBy("categories")
     .count()
     .orderBy(desc("count"))
     .select("categories")
     .limit(k)
     .collect()
     .map(row =>row.getString(0))
   }


   /**
     * Given a DataFrame of reviews, group all the reviews by product "asin"
     * and then find the average rating for each product
     * Note: column "overall" in reviews represent a rating given by one reviewer,
     * therefore you need to find the average of these given by all reviewers for the same product
     *
     * @param reviewsDF
     * @return A DataFrame that contain the columns: asin, averageRating
     */
   def findAvgProductRating(reviewsDF : DataFrame) : DataFrame = {
     reviewsDF
     .groupBy("asin")
     .agg(avg("overall")).withColumnRenamed("avg(overall)", "averageRating")
   }


   /**
     * Given the products ratings DataFrame generated using the function findAvgProductRating
     * and products metadata DataFrame, create a DataFrame all the interesting features that
     * we would like to cluster the products based on them
     * For example, if we are interested in clustering the
     * products based on tagged categories, price, and averageRating, then a DataFrame of four columns:
     * aisn, categories, price, and averageRating
     *
     * @param productsRatingsDF
     * @param productsMetadataDF
     * @return
     */
   def findProductFeatures(productsRatingsDF : DataFrame, productsMetadataDF : DataFrame) : DataFrame = {
     productsRatingsDF
       .join(productsMetadataDF, productsRatingsDF("asin")===productsMetadataDF("asin"))
       .select(productsMetadataDF("asin"), productsMetadataDF("categories"), productsMetadataDF("price"), productsRatingsDF("averageRating"))
   }


   /**
     * Given the product DataFrame and a list of top categories, prepare a features vector column
     * and return a DataFrame with only that column
     *
     * The features vector should represent the categories, average rating for a product, and any other features of your choice
     *
     * > Since there are only 5 values for the ratings, it makes sense to scale them by multiplying them by spreadVal
     * > For each category, the value of the feature will be either 0.0 or 1.0 based on whether this product is tagged based on this
     *   feature or not
     *   for example, if we have 5 categories a, b, c, d, and e and a product that has its categories column as "categories": [["a", "c"]]
     *   therefore, the representation in the features vector should be [1.0, 0.0, 1.0, 0.0, 0.0]
     *
     * To create a column out of multiple columns, you need to use a user defined function (udf) that does that.
     * The udf that you need to write its code is called createFeatureVector
     * and it takes two or more columns (categories, averageRatings, and any other columns of your interest)
     * and return one column of type Array[Double]
     *
     * Notes:
     * You will need to use "withColumn" to create a new column
     * You will need to use select or drop to project on the columns that we are interested in
     *
     * @param productData
     * @param topCategories
     * @param spreadVal
     * @return a DataFrame with one column called "features" (Array[Double]), each row represent a product
     */
   def prepareDataForClustering(productData : DataFrame, topCategories : Array[String], spreadVal : Double) : DataFrame = {
     val createFeatureVector = udf{(categories:Seq[Seq[String]], price: Double, averageRating: Double) =>
       topCategories.map(e => if (categories.flatten.contains(e)) 1.0 else 0.0) :+ price :+ (averageRating*spreadVal)
     }
     productData.withColumn("features", createFeatureVector($"categories",$"price", $"averageRating")).select("features").na.drop
   }

   /**
     * Use k-means to create a model of the input data
     * You might want to check this example:
     * https://spark.apache.org/docs/latest/ml-clustering.html#k-means
     * @param data
     * @return
     */
   def clusterUsingKmeans(data: DataFrame, k: Int): KMeansModel = {
     val kmeans = new KMeans()
       .setK(k)
       .setSeed(0)
     kmeans.fit(data)


   }
     // Trains a k-means model and then return it



   /**
     * Print the centers for a given k-mean model
     *
     * This function assumes that the features are categories + averageRating per product
     * when you experiment with other features, you might need to update this function
     *
     * We suggest that you also edit this function to also print the number of products in each cluster
     * Note the function transform (model.transform(dataset))
     * in the example https://spark.apache.org/docs/latest/ml-clustering.html#k-means
     * that returns the dataset with an added new column "prediction" representing a center of the cluster
     *
     * @param model
     * @param categories
     * @param outFilePath
     * @param spreadVal
     */
   def printKmeansCenters(model : KMeansModel, categories : Array[String],outFilePath: String,spreadVal : Double, dataset: DataFrame):Unit = {
     /** Update me! **/
     val predictions = model.transform(dataset)
     val groupedPredictions = predictions.groupBy("prediction").count().orderBy("prediction")

     //this works:
     val clusterCentersInfo = udf {(index: Int) =>
       //model.clusterCenters(index)
       //This doesn't work cuz the output is weird for pricing & rating. The toString fucks up the values
       //That's the code I've also sent to Iman
       val centers = model.clusterCenters
       val v = centers(index).toArray.splitAt(categories.length)
       val c = v._2.splitAt(1)
       val completeList = v._1.zip(categories).filter(_._1 != 0).map(p => p._2).toList ::: c._1.map(a => a.round).toList ::: c._2.map(a =>(a/spreadVal).round).toList
       completeList.toString()
     }

     // Shows the result.
     println("Printing Cluster Centers")
     //val out = new PrintWriter(outFilePath)
     //groupedPredictions.withColumn("ClusterInfo", clusterCentersInfo($"prediction")).show(false)
     //Doesn't work with writing it on the csv and it's also not a csv but just a partition (RDD), I've tried toDF before the write but that doesn't work
     groupedPredictions.withColumn("ClusterInfo", clusterCentersInfo($"prediction"))
       .coalesce(1)
       .write.format("com.databricks.spark.csv")
       .option("header", true)
       .mode("overwrite")
       .save(outFilePath)

     /*
          model
            .clusterCenters
            .foreach { cluster =>
              val v = cluster.toArray.splitAt(categories.length)
              v._1.zip(categories).filter(_._1 != 0).map(p => p._2).foreach(c => out.print(c + " * "))
              //split the second part of the tuple which includes price & rating
              val c = v._2.splitAt(1)
              //print price normal
              c._1.foreach(c => out.print(c + " * "))
              //print rating devided by spreadVal and rounded
              c._2.map(a =>(a/spreadVal).round).foreach(c => out.print(c + " * "))
              //cluster + groupedPredictions.map
              out.println
            }

          //close out file
          out.close()

          */
   }

   /**
     * Same as printKmeansCenters but writes the o/p to HDFS
     * To be used when you execute your code on the cluster
     *
     * @param model
     * @param categories
     * @param outFilePath
     * @param spreadVal
     */
   def printKmeansCentersCluster(model : KMeansModel, categories : Array[String],outFilePath: String,spreadVal: Double, dataset: DataFrame):Unit={
     import org.apache.hadoop.fs.{FileSystem,Path}

     //get the hdfs information from spark context
     val hdfs = FileSystem.get(spark.sparkContext.hadoopConfiguration)
     //create outFile in HDFS given the file name
     hdfs.create(new Path(outFilePath))
     //create a BufferedOutputStream to write to the file
    // val out = new BufferedOutputStream(outFile)

     val predictions = model.transform(dataset)
     val groupedPredictions = predictions.groupBy("prediction").count().orderBy("prediction")

     //this works:
     val clusterCentersInfo = udf {(index: Int) =>
       //model.clusterCenters(index)
       //This doesn't work cuz the output is weird for pricing & rating. The toString fucks up the values
       //That's the code I've also sent to Iman
       val centers = model.clusterCenters
       val v = centers(index).toArray.splitAt(categories.length)
       val c = v._2.splitAt(1)
       val completeList = v._1.zip(categories).filter(_._1 != 0).map(p => p._2).toList ::: c._1.map(a => a.round).toList ::: c._2.map(a =>(a/spreadVal).round).toList
       completeList.toString()
     }

     println("Printing Cluster Centers")
     //val out = new PrintWriter(outFilePath)
     //groupedPredictions.withColumn("ClusterInfo", clusterCentersInfo($"prediction")).show(false)
     //Doesn't work with writing it on the csv and it's also not a csv but just a partition (RDD), I've tried toDF before the write but that doesn't work
     groupedPredictions.withColumn("ClusterInfo", clusterCentersInfo($"prediction"))
       .coalesce(1)
       .write.format("com.databricks.spark.csv")
       .option("header", true)
       .mode("overwrite")
       .save("hdfs://" + outFilePath)
    /*
     model
       .clusterCenters
       .foreach { cluster =>
         val v = cluster.toArray.splitAt(categories.length)
         v._1.zip(categories).filter(_._1 != 0).map(p => p._2).foreach(c => out.write((c + " * ").getBytes("UTF-8")))
         val c = v._2.splitAt(1)
         c._1.foreach(c => out.write((c + " * ").getBytes("UTF-8")))
         c._2.map(a => (a/spreadVal).round).foreach(c => out.write((c + " * ").getBytes("UTF-8")))
         out.write('\n')
       }
     out.close()
      */
   }


   /**
     * Evaluate a given k-means model based on the input dataset
     * code from the example: https://spark.apache.org/docs/latest/ml-clustering.html#k-means
     * @param model
     * @param dataset
     */
   def evaluateModel(model: KMeansModel, dataset: DataFrame) :Unit ={
     // Make predictions
     val predictions = model.transform(dataset)

     // Evaluate clustering by computing Silhouette score
     val evaluator = new ClusteringEvaluator()

     val silhouette = evaluator.evaluate(predictions)
     println(s"Silhouette with squared euclidean distance = $silhouette")

   }

   /**
     * This function should call the above functions to prepare the a DataFrame of feature vectors
     * then calls clusterUsingKmeans to generate a kmeans model and return that model
     *
     * @param amazonReviewsDF
     * @param amazonMetadataDF
     * @param topKCategories
     * @param spreadValue
     * @param clustersNumber
     * @return
     */
   def clusterAmazonData(amazonReviewsDF: DataFrame, amazonMetadataDF: DataFrame, topKCategories: Array[String], spreadValue: Double, clustersNumber: Int): KMeansModel ={
     clusterUsingKmeans(prepareDataForClustering(findProductFeatures(findAvgProductRating(amazonReviewsDF),amazonMetadataDF), topKCategories, spreadValue), clustersNumber)
   }

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()

    //check that the paths for input files exist in the config, otherwise exit
    if(!config.hasPath("BIDMT.Project3.Amazon.reviewsFilePath") || ! config.hasPath("BIDMT.Project3.Amazon.metadataFilePath")){
      println("Error, configuration file does not contain the file paths for reviews and metadata files!")
      spark.close()
    }

    //read from configuration file amazon reviews dataset and metadata file paths
    val amazonReviewsFilePath = config.getString("BIDMT.Project3.Amazon.reviewsFilePath")
    val amazonMetadataFilePath = config.getString("BIDMT.Project3.Amazon.metadataFilePath")

    //load reviews and metadata
    val amazonReviewsDF = jsonDataLoader(amazonReviewsFilePath)
    val amazonMetadataDF = jsonDataLoader(amazonMetadataFilePath)
    val amazonRatingsDF = findAvgProductRating(amazonReviewsDF)
    val productData = findProductFeatures(amazonRatingsDF, amazonMetadataDF)

    //generate an array of the top k categories to be considered as features for k-means clustering
    val numCategoriesAsFeatures = config.getInt("BIDMT.Project3.Amazon.numCategoriesAsFeatures")
    val topKCategories = findTopKCategory(amazonMetadataDF,numCategoriesAsFeatures)

    //get the values for setting up k-means from the configuration file
    val spreadValue = config.getInt("BIDMT.Project3.Amazon.spreadValue")
    val clustersNumber =
      if(config.hasPath("BIDMT.Project3.Amazon.clustersNumber")) config.getInt("BIDMT.Project3.Amazon.clustersNumber")
      else topKCategories.length * 3


    /*val createFeatureVector = udf{(categories:Seq[Seq[String]], averageRating: Double) =>
      topKCategories.map(e => if (categories.flatten.contains(e)) 1.0 else 0.0) :+ (averageRating*spreadValue)
    }

    productData.withColumn("features", createFeatureVector($"categories",$"averageRating")).select("features").na.drop.show(10)
*/
    //get output file path from config file
    val outFilePath = config.getString("BIDMT.Project3.Amazon.clusterCentersOutFilePath")


    //find the kmeans model for the dataset
    val kmeansModel = clusterAmazonData(amazonReviewsDF, amazonMetadataDF, topKCategories, spreadValue, clustersNumber)


    //print or evaluate the model
    printKmeansCenters(kmeansModel, topKCategories, outFilePath, spreadValue, prepareDataForClustering(productData, topKCategories, spreadValue) )
    //evaluateModel(kmeansModel, prepareDataForClustering(productData, topKCategories, clustersNumber))


    spark.close()
  }

}
