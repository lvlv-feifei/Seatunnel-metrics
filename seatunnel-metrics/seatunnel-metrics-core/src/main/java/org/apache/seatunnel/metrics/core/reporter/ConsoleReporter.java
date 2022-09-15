package org.apache.seatunnel.metrics.core.reporter;

import org.apache.seatunnel.metrics.core.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**  A reporter which outputs measurements to log */
public class ConsoleReporter implements MetricReporter{

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleReporter.class);
    private static final String lineSeparator = System.lineSeparator();
    private int previousSize = 16384;

    @Override
    public ConsoleReporter open() {
        LOG.info("reporter open");
        return new ConsoleReporter();
    }

    @Override
    public void close() {
        LOG.info("reporter close");
    }

    @Override
    public void report(Map<Gauge, MetricInfo> gauges,
                       Map<Counter, MetricInfo> counters,
                       Map<Histogram, MetricInfo> histograms,
                       Map<Meter, MetricInfo> meters) {
        StringBuilder builder = new StringBuilder((int) (previousSize * 1.1));

        builder.append(lineSeparator)
                .append(
                        "=========================== Starting metrics report ===========================")
                .append(lineSeparator);

        builder.append(lineSeparator)
                .append(
                        "-- Counters -------------------------------------------------------------------")
                .append(lineSeparator);

        for (Map.Entry<Counter, MetricInfo> metric : counters.entrySet()) {
            builder.append(metric.getValue().toString())
                    .append(": ")
                    .append(metric.getKey().getCount())
                    .append(lineSeparator);
        }


        LOG.info(builder.toString());

        previousSize = builder.length();

    }


}
