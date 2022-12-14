# Seatunnel-Metrics 接口文档
## 1. MetricReporter
1. MetricReporter是seatunnel将metrics发送到外部的接口；用户可以根据外部系统实现该接口以获取seatunnel指标
1. 接口定义情况
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

3. open：配置外部接口的函数，读取用户输入配置，进行初始化
3. close：停止metrics输出
3. report：核心方法，其中四个参数分别为seatunnel抓取的所有指标信息，用户可以根据自己需要进行过滤封装处理等
### 2. 参考实现一 ConsoleReporter

1.  ConsoleReporter为将指标信息打印到控制台的方法
1. 简单的对四个map遍历并继续周期的打印输出
1. 示例输出如下

![image.png](https://cdn.nlark.com/yuque/0/2022/png/12643173/1663483564424-116d3e15-ce6e-4ab8-8856-06608d63bfa1.png#clientId=ud9e75444-b93b-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=474&id=ufb64d745&margin=%5Bobject%20Object%5D&name=image.png&originHeight=948&originWidth=1278&originalType=binary&ratio=1&rotation=0&showTitle=false&size=132040&status=done&style=none&taskId=u03ec3cca-701f-421b-9415-e4ef13c1657&title=&width=639)
### 3. 参考实现二 PrometheusPushGatewayReporter

1. PrometheusPushGatewayReporter为对Prometheus的实现
1. 其中report中先遍历四个map将Metric和MetricInfo进行总和注册
1. 再通过pushgateway推送到Prometheus中
1. 示例输出如下

![image.png](https://cdn.nlark.com/yuque/0/2022/png/12643173/1663483649022-731a6f8e-f698-4e78-bcf1-6b4769710bee.png#clientId=ud9e75444-b93b-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=790&id=u4422a6f1&margin=%5Bobject%20Object%5D&name=image.png&originHeight=1580&originWidth=2878&originalType=binary&ratio=1&rotation=0&showTitle=false&size=452055&status=done&style=none&taskId=u6f4b0b43-6f01-4b93-9f45-566c00fc77c&title=&width=1439)

 
 

 

