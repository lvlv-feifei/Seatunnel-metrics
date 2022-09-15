import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.time.Duration;

import static org.apache.flink.configuration.ConfigOptions.key;

public class WindowWordCount {

    public static void main(String[] args) throws Exception {

        //构建flink-metrics参数
        ConfigOption<String> REPORTERS_LIST =
                key("metrics.reporters")
                        .stringType()
                        .noDefaultValue()
                        .withDescription(
                                "An optional list of reporter names. If configured, only reporters whose name matches"
                                        + " any of the names in the list will be started. Otherwise, all reporters that could be found in"
                                        + " the configuration will be started.");

        ConfigOption<String> REPORTER_CLASS =
                key("metrics.reporter.seatunnel_reporter.class")
                        .stringType()
                        .noDefaultValue()
                        .withDescription("The reporter class to use for the reporter named <name>.");
        ConfigOption<Duration> REPORTER_INTERVAL =
                key("metrics.reporter.seatunnel_reporter.interval")
                        .durationType()
                        .defaultValue(Duration.ofSeconds(10))
                        .withDescription("The reporter interval to use for the reporter named <name>.");

        ConfigOption<String> REPORTER_CONFIG_PARAMETER =
                key("metrics.reporter.seatunnel_reporter.port")
                        .stringType()
                        .noDefaultValue()
                        .withDescription(
                                "Configures the parameter <parameter> for the reporter named <name>.");
        Duration duration = Duration.ofSeconds(30);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(new Configuration().set(REPORTERS_LIST, "seatunnel_reporter").set(REPORTER_CLASS, "org.apache.seatunnel.metrics.flink.SeatunnelMetricReporter").set(REPORTER_INTERVAL, duration));
        //StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(new Configuration().set(REPORTERS_LIST,"seatunnel_reporter").set(REPORTER_CLASS,"org.apache.flink.metrics.slf4j.Slf4jReporter").set(REPORTER_INTERVAL,duration));
        //StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(new Configuration().set(REPORTERS_LIST,"seatunnel_reporter").set(REPORTER_CLASS,"org.apache.flink.metrics.prometheus.PrometheusReporter").set(REPORTER_INTERVAL,duration).set(REPORTER_CONFIG_PARAMETER,"9999"));
        //StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        DataStream<Tuple2<String, Integer>> dataStream = env
                .socketTextStream("localhost", 9200)
                .flatMap(new Splitter())
                .keyBy(value -> value.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .sum(1);

        dataStream.print();

        env.execute("Window WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word : sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

}
