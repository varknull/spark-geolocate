import kdtree.KDTree
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


object GeolocateByIATA {

  def findClosest(lat: Double, long: Double, tree: Broadcast[KDTree]): String = {
    val nearests = tree.value.searchKNeighbors(lat, long ,1)
    nearests(0).data
  }

  def main(args: Array[String]) {
    var path_prefix = ""
    if (args.length == 1) {
      path_prefix = args.toList(0)
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

    val dfOptd = spark.read
      .option("header", "true")
      .csv(path_prefix+"sample_data/optd-sample-20161201.csv.gz")
      .collect()

    val kdtree = new KDTree()
    dfOptd.foreach(r => {
      kdtree.insert(r.getString(1).toDouble, r.getString(2).toDouble, r.getString(0))
    })
    val optdBroadcast = spark.sparkContext.broadcast(kdtree)

    val dfData = spark
      .read
      .option("header", "true")
      .csv(path_prefix+"sample_data/source/")

    val findClosestIata = udf((lat: Double, long: Double) => findClosest(lat, long, optdBroadcast))

    dfData
      .withColumn("iata_code", findClosestIata(dfData("geoip_latitude"), dfData("geoip_latitude")))
      .select("uuid", "iata_code")
      .write.format("com.databricks.spark.csv").mode("overwrite").option("header", "true").save(path_prefix+"output_csv")
  }
}
