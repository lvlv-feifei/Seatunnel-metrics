package org.apache.seatunnel.metrics.core;

import java.util.List;

/** Stores all child-properties of a metric. */
public class MetricInfo {
    private String metricName;

    public String getMetricName() {
        return metricName;
    }

    public String getHelpString() {
        return helpString;
    }

    public List<String> getDimensionKeys() {
        return dimensionKeys;
    }

    public List<String> getDimensionValues() {
        return dimensionValues;
    }

    private String helpString;
    List<String> dimensionKeys;
    List<String> dimensionValues;

    public MetricInfo(String metricName, String helpString, List<String> dimensionKeys, List<String> dimensionValues) {
        this.metricName = metricName;
        this.helpString = helpString;
        this.dimensionKeys = dimensionKeys;
        this.dimensionValues = dimensionValues;
    }
}
