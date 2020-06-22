package com.rdc.zrj.flow.hystrixcommand;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;

import java.util.concurrent.Future;

/**
 * @author leviathanstan
 * @date 05/03/2020 21:07
 */
public class BatchTest {

    @Test
    public void testBatch() throws Exception{
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        ServiceProvider provider = new ServiceProvider();
        ServiceCollapseCommand b1 = new ServiceCollapseCommand(provider, 1);
        ServiceCollapseCommand b2 = new ServiceCollapseCommand(provider, 1);
        ServiceCollapseCommand b3 = new ServiceCollapseCommand(provider, 1);
        ServiceCollapseCommand b4 = new ServiceCollapseCommand(provider, 1);
        Future<String> q1 = b1.queue();
        Future<String> q2 = b2.queue();
        Future<String> q3 = b3.queue();
        Thread.sleep(3000);
        Future<String> q4 = b4.queue();
        System.out.println(q1.get());
        System.out.println(q2.get());
        System.out.println(q3.get());
        System.out.println(q4.get());
        context.close();
    }
}
