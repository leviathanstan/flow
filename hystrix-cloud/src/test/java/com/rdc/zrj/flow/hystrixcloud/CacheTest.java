package com.rdc.zrj.flow.hystrixcloud;

import com.rdc.zrj.flow.hystrixcloud.service.UserService;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Future;

/**
 * @author leviathanstan
 * @date 05/03/2020 20:35
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HystrixApplication.class)
public class CacheTest {

    @Autowired
    UserService userService;

    @Test
    public void testCollapser() throws Exception{
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        Future<String> f1 = userService.getUser6(3);
        Future<String> f2 = userService.getUser6(4);
        Future<String> f3 = userService.getUser6(5);

        Thread.sleep(1000);
        Future<String> f4 = userService.getUser6(6);
        System.out.println(f1.get());
        System.out.println(f2.get());
        System.out.println(f3.get());
        System.out.println(f4.get());
        context.close();
    }

    @Test
    public void testRest() {
        System.out.println(userService.getRest());
    }
}
