# Seatunnel-Metrics 接口文档
## MetricReporter
1. MetricReporter
2. 接口定义情况
```java
public interface MetricReporter {
MetricReporter open();

    void close();

    void report(Map<Gauge, MetricInfo> gauges,
                Map<Counter, MetricInfo> counters,
                Map<Histogram, MetricInfo> histograms,
                Map<Meter, MetricInfo> meters);
}
```
