package org.apache.seatunnel.metrics.core;


import java.util.HashMap;

public interface Histogram extends Metric{
    long getCount();
    double getMin();
    double getMax();
    double getStdDev();
    double getMean();
    HashMap<Double, Double> getQuantile();

    default MetricType getMetricType() {
        return MetricType.HISTOGRAM;
    }

}
