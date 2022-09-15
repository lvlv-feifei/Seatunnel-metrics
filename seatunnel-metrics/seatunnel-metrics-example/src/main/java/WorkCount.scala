import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object WorkCount {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      //.config("spark.extraListeners","wc.ListenerDemo1")
      .config("spark.metrics.conf.*.sink.console.class", "org.apache.seatunnel.metrics.spark.MineConsoleSink")
      .config("spark.metrics.conf.*.source.jvm.class","org.apache.spark.metrics.source.JvmSource")
      .enableHiveSupport()
      .getOrCreate()

    val wordString = Array("hadoop", "hadoop", "spark","spark","spark","spark","flink","flink","flink","flink",
      "flink","flink","hive","flink","hdfs","yarn","zookeeper","hbase","impala","sqoop","hadoop")

    //生成Rdd
    val wordRdd: RDD[String] = spark.sparkContext.parallelize(wordString)

    //统计wordcount
    val resRdd: RDD[(String, Int)] = wordRdd.map((_, 1)).reduceByKey(_ + _)

    resRdd.foreach(elem => {
      println(elem._1 + "-----" + elem._2)
    })

    spark.stop()
  }

}

