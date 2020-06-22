package com.rdc.zrj.flow.hystrixcommand;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

/**
 * @author leviathanstan
 * @date 05/02/2020 23:28
 */
public class CacheCommand extends HystrixCommand<String> {
    private String cacheKey;
    public CacheCommand(String cacheKey) {
        super(Setter.withGroupKey(
                HystrixCommandGroupKey.Factory.asKey("TestGroupKey"))
                .andCommandKey(
                        HystrixCommandKey.Factory.asKey("MyCommandKey")));
        this.cacheKey = cacheKey;
    }
    @Override
    protected String run() throws Exception {
        System.out.println("执行方法");
        return "";
    }
    @Override
    protected String getFallback() {
        System.out.println("执行回退");
        return "";
    }
    @Override
    protected String getCacheKey() {
        return this.cacheKey;
    }

}