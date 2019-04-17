import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import com.sizmek.rtree2d.core._
import EuclideanPlane._

object GeolocateByIATA {

  def findClosest(lat: Float, long: Float, tree: RTree[String]): String = {
    val nearest = tree.nearestK(lat, long, k=1)
    nearest(0).value
  }

  def main(args: Array[String]) {
    var path_prefix = ""
    if (args.length == 1) {
      path_prefix = args(0)
    }

    //Create a SparkContext to initialize Spark
    val conf: SparkConf = new SparkConf()
      .setMaster("local[4]")
      .setAppName("GeolocateByIATA")
      .set("spark.driver.allowMultipleContexts", "false")
      .set("spark.ui.enabled", "false")
      .set("spark.sql.streaming.schemaInferenc‌​e","true")

    val spark: SparkSession = SparkSession.builder()
      .config(conf)
      .getOrCreate()

    // get coordinates as Array[entry] to build the Rtree
    val coordinates = spark.read
      .option("header", "true")
      .csv(path_prefix+"sample_data/optd-sample-20161201.csv.gz")
      .collect()
      .map(r => entry(r.getString(1).toFloat, r.getString(2).toFloat, r.getString(0)))

    // to broadcast the tree
    val rtreeBroadcast = spark.sparkContext.broadcast(RTree(coordinates))

    val dfData = spark
      .read
      .option("header", "true")
      .csv(path_prefix+"sample_data/source/")

    val findClosestIata = udf((lat: Float, long: Float) => findClosest(lat, long, rtreeBroadcast.value))

    dfData
      .withColumn("iata_code", findClosestIata(dfData("geoip_latitude"), dfData("geoip_latitude")))
      .select("uuid", "iata_code")
      .write.format("com.databricks.spark.csv").mode("overwrite").option("header", "true").save(path_prefix+"output_csv")
  }
}
