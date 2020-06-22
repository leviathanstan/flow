package com.rdc.zrj.flow.hystrixcommand;

import com.netflix.hystrix.*;

/**
 * @author leviathanstan
 * @date 04/29/2020 21:10
 */
public class MyHystrixCommand extends HystrixCommand<String> {

    private ServiceProvider serviceProvider;

    public MyHystrixCommand(ServiceProvider serviceProvider) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("service"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("select"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerRequestVolumeThreshold(10)//至少有10个请求，熔断器才进行错误率的计算
                        .withExecutionTimeoutInMilliseconds(500)
                        .withCircuitBreakerSleepWindowInMilliseconds(5000)//熔断器中断请求5秒后会进入半打开状态,放部分流量过去重试
                        .withCircuitBreakerErrorThresholdPercentage(30)//错误率达到50开启熔断保护
                        .withExecutionTimeoutEnabled(true)
                        .withExecutionIsolationThreadInterruptOnFutureCancel(true))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties
                        .Setter().withCoreSize(10)));
        this.serviceProvider = serviceProvider;
    }

    @Override
    protected String run() {
        return serviceProvider.doRun();
    }

    @Override
    protected String getFallback() {
        return "error";
    }

}
