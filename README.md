# Talos

在微服务越为常用的今天，仅通过日志进行运维投入大量的人力，系统初期很难对开发人员要求在编码时输出用于运维阶段定位分析问题的日志，即使有了成熟的日志规范，也很难保证落实到位。尤其是系统成熟之后，如果还要考虑维护系统日志，这是非常头疼的，Talos能有效的解决这一问题。


[Google Dapper](http://research.google.com/pubs/pub36356.html)是业界做全链路监控的理论基础，16年我们逐步向微服务迈进时预言过一年后产线将运行上百个应用节点，于是着手Talos的调研工作，Twitter开源的[Brave](https://github.com/openzipkin/brave)是一个大而全的分布式跟踪系统，但Brave没有提供对Dubbo的支持，而我们分布式框架使用的正是Dubbo，并且在此之前我们的开发框架已经有一些框架性的日志可以复用，因此我们决定参考Brave，自研适用于现有开鑫金服技术架构的全链路监控系统。
【特别说明：Talos系统中有引用Brave源码，主要参考了Brave中Trace/Span的模型定义，代码位于talos/talos-brave这个module。】

Talos系统是一个小而简的全链路监控系统，我们利用logback异步输出日志，直接将日志输出至kafka，talos-storage从kafka拉数据并存储至es和hbase，talos-dashboard通过es和hbase的数据进行展示，技术架构及部署架构移步[Talos架构设计](https://kplxq.github.io/2017/12/15/%E6%9E%B6%E6%9E%84%E8%AE%BE%E8%AE%A1/)

## 技术交流

QQ交流群：637375352

微信公众号：
![](https://kplxq.github.io/img/wx_gzh.jpg)

## 适用场景

### 技术栈

- Java (1.7+)
- SpringMVC (3.1.1.RELEASE)
- Dubbo (2.5.4)
- Logback (1.1.7)
- Maven (Maven3.1)
- Elasticsearch(2.4.0)
- HBase(1.2.0)
- Kafka(2.11)

### 配置模式

- 基于Spring AOP，完成SpringMVC应用内部调用的打点。
- 基于Dubbo Filter，完成系统间Dubbo调用的打点。
- 基于Http Filter，完成系统间Http调用的打点。

## 快速使用

### Talos接入说明

移步 [Talos接入说明](https://kplxq.github.io/2017/12/15/Talos%E6%8E%A5%E5%85%A5%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E/)

### talos-sample

移步 [talos-sample](https://github.com/kplxq/talos/tree/master/talos-sample)

## 效果展示

首页：
![dashboard 首页](https://kplxq.github.io/img/talos/index.png)

搜索页：
![dashboard 搜索页](https://kplxq.github.io/img/talos/searchpage.png)

调用链：
![dashboard 调用链页](https://kplxq.github.io/img/talos/treepage.png)

健康度：
![dashboard 健康度](https://kplxq.github.io/img/talos/monitor.png)
