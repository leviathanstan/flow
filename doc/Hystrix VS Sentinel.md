# Hystrix VS Sentinel

由于sentinel的[官方文档](https://github.com/alibaba/Sentinel/wiki/介绍) 较为详尽，不做重复搬运；

关于Hystrix的开发可看[Hystrix开发指南](Hystrix开发指南.md)；

关于Sentinel的实践系列：

- [Sentinel限流原理](Sentinel限流原理.md)

- [Sentinel集群实践](Sentinel集群实践.md)  

Hystrix和Sentinel与各框架的适配demo见：[flow-demo](https://github.com/leviathanstan/flow.git)

### 前景

Hystrix已于2018年停止开发，版本停留在1.5.18。

2019年，Sentinel 加入了 Spring Cloud Circuit Breaker，得到官方支持。

### api

在api的使用上，无论是以注解的形式使用，抑或与其它框架适配（如gateway、feign、webflux）的形式，它们二者都没有太大的区别。实际业务开发中，不会有太大的代码量的差别。常规方式上，二者都对业务代码有一定入侵度。

### 规则配置

规则保存方式上：

1. sentinel的配置保存的方式是json的形式
2. Hystrix是yml这种形式为主流。

修改配置的方式上：

1. sentinel主要提供两种方式：一是通过硬编码的方式设置规则；二是通过控制台的图形化界面来进行规则的设置，控制台配置的规则可根据需要保存到不同的数据源（内存、文件、数据库）。理论上只要符合sentinel中的json格式，我们可以自行实现其它配置方式。
2. Hystrix主要可提供硬编码、注解、配置文件这三种方式。其中注解方式需要contrib模块支持；springcloud中支持yml形式的配置。

动态配置上：

1. sentinel推荐在生产环境中使用push的模式实现配置的动态更新（[在生产环境中使用-Sentinel](https://github.com/alibaba/Sentinel/wiki/在生产环境中使用-Sentinel)），但需要我们自己实现配置中心，控制台如果要建集群也需要自行实现session共享等问题。
2. Hystrix也支持不停机更新，动态配置可以使用archaius来实现，以pull的形式，当然也需要一个配置中心。

### 功能

sentinel官方有表格式的对比：[从 Hystrix 迁移到 Sentinel](https://github.com/alibaba/Sentinel/wiki/Guideline:-从-Hystrix-迁移到-Sentinel)

总的来说，sentinel的功能要多很多。下面只说一下sentinel和Hystrix重叠和Hystrix独有的功能：

1. Hystrix提供缓存的功能。但是过于鸡肋。
2. Hystix提供请求合并功能。但是请求合并其实是为了减少线程的频繁上下文切换而设置的，而sentinel中本就没有使用线程隔离的方式，所以这也不算什么优势。
3. Hystrix的特色在于：可以以线程隔离的方式对资源进行隔离。但sentinel可以使用并发线程数模式+超时熔断来达到差不多的功能。
4. Hystrix和Sentinel都提供熔断功能，在Hystrix基于错误率熔断的基础上，sentinel提供了基于平均响应时间、平均错误请求数来进行熔断。
5. Hystrix和Sentinel都拥有指标统计功能，但二者实现不同。前者基于Rxjava。

### 性能

无论是Hystrix还是Sentinel，都需要在业务基础上进行一系列的统计、检查操作，单机QPS越大，性能损失越大。如主要提供缓存访问这种本身访问速度很快的服务，二者都不适宜使用。

而Hystrix使用线程隔离方式时，会带来额外的线程上下文切换，分配的线程越多，性能降低越多。如需使用这个隔离方式，推荐使用控制台监控的方式，找出最佳的配置方式。

[Sentinel Benchmark](https://github.com/alibaba/Sentinel/wiki/Benchmark)

### 控制台

Hystrix提供的控制台可以提供诸如请求数、延迟时间、线程池状态等参数，但不提供数据持久化功能，在集群环境，可使用turebine聚合指标。

Sentinel方面，在指标可视化基础上提供了直接修改规则的功能，可自行扩展到支持集群和配置中心；默认也不提供持久化，但可自己扩展实现。

在鉴权方面，Hystrix不提供鉴权功能，sentinel只提供简单的登录权限限制，如果对鉴权有额外要求，二者都需要进行自行扩展。

### 集群

在集群的支持方面，二者都需要自行实现扩展来支持。

Hystrix的方向在于：实现统一的配置中心，并结合动态更新的方式将修改后的配置pull到单体应用；结合控制台使用，可以找出适合机器的最佳配置。

Sentinel的实现思路也差不多，只不过sentinel修改配置的侧重点在于：可以方便的对各种规则CRUD，以支持业务需求或者维持机器稳定。

### 扩展

二者都可以做一定的扩展，目前还没有进行相应实践，从文档来看，sentinel的扩展更为方便。

### 总结

Sentinel相对于Hystrix来说提供了很多额外的功能，如果对这些功能有需求（如qps限流、集群限流等），则可以选择sentinel。如果只是想要一个简单的熔断限流组件，则Hystrix可以满足要求。

对于sentinel而言，我觉得如果要用那就要结合控制台来用，因为sentinel提供的规则修改功能结合控制台界面来使用才得心应手。但是首先需要对其进行一定的改造，集群环境下改造的点要更多一点。

而Hystrix的控制台本身只提供指标展示功能，如果能通过压测等方式确定好合适的配置，那么控制台其实也不是很必要，且如果需要，使用turbine来进行集群的扩展也挺简单。

对于Hystrix的线程上下文问题，可以通过请求合并、动态配置更新、改用信号量隔离减少或规避其带来的性能损耗。