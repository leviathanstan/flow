package com.rdc.zrj.flow.hystrixcommand;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;

/**
 * @author leviathanstan
 * @date 05/02/2020 23:26
 */
public class CacheTest {


    @Test
    public void testCache() {
        HystrixRequestContext ctx = HystrixRequestContext.initializeContext();
        String key = "cache-key";
        CacheCommand c1 = new CacheCommand(key);
        CacheCommand c2 = new CacheCommand(key);
        CacheCommand c3 = new CacheCommand(key);
        c1.execute();
        c2.execute();
        c3.execute();

        System.out.println("命令c1，是否读取缓存：" + c1.isResponseFromCache());
        System.out.println("命令c2，是否读取缓存：" + c2.isResponseFromCache());
        System.out.println("命令c3，是否读取缓存：" + c3.isResponseFromCache());

//        HystrixRequestCache cache = HystrixRequestCache.getInstance(HystrixCommandKey.Factory.asKey("MyCommandKey"),
//        HystrixConcurrencyStrategyDefault.getInstance());
//        cache.clear(key);
        CacheCommand c4 = new CacheCommand(key);
        c4.execute();
        System.out.println("命令c4，是否读取缓存：" + c4.isResponseFromCache());

//        ctx.shutdown();
    }
}
