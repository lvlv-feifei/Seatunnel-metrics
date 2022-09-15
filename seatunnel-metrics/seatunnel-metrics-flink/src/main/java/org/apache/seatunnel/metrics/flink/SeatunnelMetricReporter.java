package org.apache.seatunnel.metrics.flink;

import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricConfig;
import org.apache.flink.metrics.reporter.Scheduled;
import org.apache.seatunnel.metrics.core.*;
import org.apache.seatunnel.metrics.core.reporter.MetricReporter;
import org.apache.seatunnel.metrics.core.reporter.PrometheusPushGatewayReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** exports Flink metrics to Seatunnel */
public class SeatunnelMetricReporter extends AbstractSeatunnelReporter implements Scheduled {
    private static final Logger log = LoggerFactory.getLogger(SeatunnelMetricReporter.class);
    private MetricReporter reporter;

    @Override
    public void open(MetricConfig metricConfig) {
        //String string = metricConfig.getString("name", "de");
        //log.info("StreamMetricReporter init:{}", string);
    }

    @Override
    public void close() {
        log.info("StreamMetricReporter close");
    }

    @Override
    public void report() {
        log.info("reporter report");
        HashMap<org.apache.seatunnel.metrics.core.Counter,MetricInfo> countersIndex = new HashMap<>();
        HashMap<org.apache.seatunnel.metrics.core.Gauge,MetricInfo> gaugesIndex = new HashMap<>();
        HashMap<org.apache.seatunnel.metrics.core.Histogram,MetricInfo> histogramsIndex = new HashMap<>();
        HashMap<org.apache.seatunnel.metrics.core.Meter,MetricInfo> metersIndex = new HashMap<>();

        HashSet<String>name = new HashSet<>();

        //Convert flink metrics to seatunnel
        for (Map.Entry<Counter, MetricInfo> metric : counters.entrySet()) {
            //Skip processing on a repeat
            if(name.contains(metric.getValue().getMetricName())){
                continue;
            }
            name.add(metric.getValue().getMetricName());
            countersIndex.put(new SimpleCounter(metric.getKey().getCount()),metric.getValue());
        }

        name.clear();
        for (Map.Entry<org.apache.flink.metrics.Gauge<?>, MetricInfo> metric : gauges.entrySet()) {
            //Skip processing on a repeat
            if(name.contains(metric.getValue().getMetricName())){
                continue;
            }
            name.add(metric.getValue().getMetricName());
            Object num = metric.getKey().getValue();
            if (num instanceof Number) {
                gaugesIndex.put(new SimpleGauge((Number) num),metric.getValue());
            }else{
                log.warn(metric.getKey().toString()+" is not instanceof number. It is" + metric.getKey().getClass());
            }
        }

        name.clear();
        for (Map.Entry<org.apache.flink.metrics.Meter, MetricInfo> metric : meters.entrySet()) {
            //Skip processing on a repeat
            if(name.contains(metric.getValue().getMetricName())){
                continue;
            }
            name.add(metric.getValue().getMetricName());
            metersIndex.put(new SimpleMeter(metric.getKey().getRate(),metric.getKey().getCount()),metric.getValue());

        }

        //todo histogram
        for (Map.Entry<org.apache.flink.metrics.Histogram, MetricInfo> metric : histograms.entrySet()) {
            org.apache.flink.metrics.Histogram key = metric.getKey();
            HashMap<Double,Double> quantile = new HashMap<>();
            quantile.put(0.5,key.getStatistics().getQuantile(0.5));
            quantile.put(0.75,key.getStatistics().getQuantile(0.75));
            quantile.put(0.95,key.getStatistics().getQuantile(0.95));
            histogramsIndex.put(new SimpleHistogram(key.getCount(),key.getStatistics().getMin(),key.getStatistics().getMax(),key.getStatistics().getStdDev(),key.getStatistics().getMean(),quantile),metric.getValue());
        }
        //todo handle user config
        //reporter = reporter.open();
        reporter = new PrometheusPushGatewayReporter("prometheus_job5","localhost",9091);
        reporter.report(gaugesIndex,countersIndex,histogramsIndex,metersIndex);

    }

}

