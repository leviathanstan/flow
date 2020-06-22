package com.rdc.zrj.flow.hystrixcloud.service;

import com.rdc.zrj.flow.hystrixcloud.client.AidRestClient;
import com.rdc.zrj.flow.hystrixcloud.util.UserContextHolder;
import com.netflix.hystrix.contrib.javanica.annotation.*;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

/**
 * @author leviathanstan
 * @date 04/29/2020 15:26
 */
@Service
public class UserService {

    @Autowired
    AidRestClient restTemplate;

    private void randomSleep() {
        Random random = new Random();
        int res = random.nextInt(4);
        try {
            //1/3的概率陷入超时
            if (res <= 3)   Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @HystrixCommand
    public String getRest() {
        randomSleep();
        return restTemplate.getResponse(3);
    }

    /**
     * HystrixObservableCommand使用测试
     * @param id
     * @return
     */
    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER, fallbackMethod = "observFailed") //使用observe()执行方式
    public Observable<String> getUserById(final Long id) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onNext("张三的ID:");
//                        int i = 1 / 0; //抛异常，模拟服务降级
                        subscriber.onNext(String.valueOf(id));
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private String observFailed(Long id) {
        return "observFailed---->" + id;
    }

    /**
     * LAZY参数表示使用toObservable()方式执行
     */
    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.LAZY, fallbackMethod = "observFailed") //表示使用toObservable()执行方式
    public Observable<String> getUserByName(final String name) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if(!subscriber.isUnsubscribed()) {
                        subscriber.onNext("找到");
                        subscriber.onNext(name);
//                        int i = 1/0; ////抛异常，模拟服务降级
                        subscriber.onNext("了");
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @HystrixCommand(commandKey = "user", commandProperties = {
        @HystrixProperty(name = "execution.timeout.enabled", value = "false")
    })
    public String getUser7() {
        randomSleep();
        return "getUser7";
    }

    /**
     * 缓存测试
     * @param id
     * @return
     */
    @CacheResult
    @HystrixCommand
    public String getCache1(@CacheKey int id) {
        System.out.println(UserContextHolder.userContext.get());
        return "success";
    }

    @HystrixCommand
    public String getCache2(int id) {
        System.out.println(UserContextHolder.userContext.get());
        return "success";
    }

    /**
     * 默认调用时间超过1000ms后中断
     * 不指定线程池的情况下，会将所有线程放到同一个线程池（10个线程）
     */
    @HystrixCommand
    public String getUser1(int id) {
        randomSleep();
        System.out.println(UserContextHolder.userContext.get());
        return "success";
    }

    /**
     * 请求合并测试
     * @param id
     * @return
     */
    @HystrixCollapser(batchMethod = "getResponse",collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds",value = "1000")})
    public Future<String> getUser6(Integer id) {

        return null;
    }

    /**
     * 请求合并调用接口
     * @param ids
     * @return
     */
    @HystrixCommand
    public List<String> getResponse(List<Integer> ids) {
        System.out.println(Thread.currentThread().getName());
        List<String> res = new ArrayList<>(ids.size());
        for (int i : ids) {
            res.add("id:" + String.valueOf(i));
        }
        return res;
    }


    /**
     * 断路器模式
     * 设置超时时间12s
     */
    @HystrixCommand(
            commandProperties = {
                    @HystrixProperty(
                            name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "12000")
            })
    public String getUser2() {
        randomSleep();
        return "success";
    }
    /**
     * 后备模式
     * 后备方法必须和原方法放到同一个类，且方法返回类型和方法签名中的参数列表必须一致
     */
    @HystrixCommand(fallbackMethod = "buildFallbackLicense")
    public String getUser3() {
        System.out.println("command" + Thread.currentThread().getName());
        randomSleep();
        return "success";
    }

    /**
     * 舱壁模式
     * threadPoolProperties:参数为可选，不设置则使用默认值。
     * HystrixProperty的key、value和ThreadPoolExecutor的构造方法形参类似
     * maxQueueSize。值为-1表示使用SynchronousQueue，大于1表示使用LinkedBlockingQueue
     * maxQueueSize的值设置后便不可修改，而线程队列长度参数可使用queueSizeRejectionThreshold属性来动态修改
     * 线程池大小设置：健康状态下最大处理请求数 * 99%延迟时间 + 用于缓冲的少量线程
     */
    @HystrixCommand(
            threadPoolKey = "licensePool",threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),
                    @HystrixProperty(name = "maxQueueSize", value = "10")
            })
    public String getUser4() {
        randomSleep();
        return "success";
    }

    /**
     * 配置断路器(commandProperties)行为
     *
     */
    @HystrixCommand(
            fallbackMethod = "buildFallbackLicense",
            threadPoolKey = "usePool",
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),
                    @HystrixProperty(name = "maxQueueSize", value = "10")},
            commandProperties = {
                    //跳闸前时间窗口内的服务最小调用次数，默认20次
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
                    //最小错误百分比，默认50%
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"),
                    //跳闸后新的时间窗口时间，默认5s
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
                    //发生调用失败后启动的时间窗口大小，默认10s
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"),
                    //一个时间窗口内桶的个数
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5"),
                    //使用信号量的隔离方式，默认为THREAD
//                    @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
            })
    public String getUser5() {
        randomSleep();
        return "success";
    }

    @HystrixCommand
    public String buildFallbackLicense(Throwable throwable) {
        System.out.println(throwable.getMessage());
        System.out.println("fallback" + Thread.currentThread().getName());
        return "degrade";
    }
}
