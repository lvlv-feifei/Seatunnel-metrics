package org.apache.seatunnel.metrics.spark

import com.codahale.metrics._
import org.apache.spark.internal.Logging

import java.util
import java.util.concurrent.TimeUnit
import java.util.{Locale, Properties}
import scala.collection.JavaConverters.asScalaSetConverter

object SeatunnelMetricSink {
  trait SinkConfig extends Serializable {
    def metricsNamespace: Option[String]

    def sparkAppId: Option[String]

    def sparkAppName: Option[String]

    def executorId: Option[String]
  }
}

abstract class SeatunnelMetricSink(property: Properties,
                                   registry: MetricRegistry,
                                   sinkConfig: SeatunnelMetricSink.SinkConfig,
                                  ) extends Logging {

  import sinkConfig._

  protected class SeatunnelMetricReporter(registry: MetricRegistry,
                                          metricFilter: MetricFilter)
    extends ScheduledReporter(
      registry,
      "seatunnel-reporter",
      metricFilter,
      TimeUnit.SECONDS,
      TimeUnit.MILLISECONDS) {

    override def report(gauges: util.SortedMap[String, Gauge[_]],
                        counters: util.SortedMap[String, Counter],
                        histograms: util.SortedMap[String, Histogram],
                        meters: util.SortedMap[String, Meter],
                        timers: util.SortedMap[String, Timer]): Unit = {

      logInfo(s"metricsNamespace=$metricsNamespace, sparkAppName=$sparkAppName, sparkAppId=$sparkAppId, " +
        s"executorId=$executorId")

      val role: String = (sparkAppId, executorId) match {
        case (Some(_), Some("driver")) | (Some(_), Some("<driver>")) => "driver"
        case (Some(_), Some(_)) => "executor"
        case _ => "unknown"
      }

      val job: String = role match {
        case "driver" => metricsNamespace.getOrElse(sparkAppId.get)
        case "executor" => metricsNamespace.getOrElse(sparkAppId.get)
        case _ => metricsNamespace.getOrElse("unknown")
      }

      val instance: String = "instance"
      val appName: String = sparkAppName.getOrElse("")

      logInfo(s"role=$role, job=$job")

    }

  }

  val CONSOLE_DEFAULT_PERIOD = 10
  val CONSOLE_DEFAULT_UNIT = "SECONDS"

  val CONSOLE_KEY_PERIOD = "period"
  val CONSOLE_KEY_UNIT = "unit"

  val KEY_RE_METRICS_FILTER = "metrics-filter-([a-zA-Z][a-zA-Z0-9-]*)".r

  val pollPeriod = Option(property.getProperty(CONSOLE_KEY_PERIOD)) match {
    case Some(s) => s.toInt
    case None => CONSOLE_DEFAULT_PERIOD
  }

  val pollUnit: TimeUnit = Option(property.getProperty(CONSOLE_KEY_UNIT)) match {
    case Some(s) => TimeUnit.valueOf(s.toUpperCase(Locale.ROOT))
    case None => TimeUnit.valueOf(CONSOLE_DEFAULT_UNIT)
  }


  val metricsFilter: MetricFilter = MetricFilter.ALL

  val reporter = new SeatunnelMetricReporter(registry, metricsFilter)

  def start(): Unit = {
    reporter.start(pollPeriod, pollUnit)
  }

  def stop(): Unit = {
    reporter.stop()
  }

  def report(): Unit = {
    reporter.report()
  }

}
