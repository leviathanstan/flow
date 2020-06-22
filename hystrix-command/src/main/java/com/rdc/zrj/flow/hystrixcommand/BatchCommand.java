package com.rdc.zrj.flow.hystrixcommand;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import java.util.List;

/**
 * @author leviathanstan
 * @date 05/03/2020 20:48
 */
public class BatchCommand extends HystrixCommand<List<String>> {
    private List<Integer> ids;
    private ServiceProvider provider;

    public BatchCommand(List<Integer> ids, ServiceProvider provider) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("collapse"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("cKey")));
        this.ids = ids;
        this.provider = provider;
    }

    @Override
    protected List<String> run() {
        return provider.batch(ids);
    }
}
