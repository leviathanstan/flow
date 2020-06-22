package com.rdc.zrj.flow.hystrixdynamic.service;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author leviathanstan
 * @date 04/29/2020 15:26
 */
@Service
public class UserService {

    private void randomSleep() {
        Random random = new Random();
        int res = random.nextInt(4);
        try {
            if (res <= 3)   Thread.sleep(2000);
        } catch (InterruptedException e) {
            //ignore
            System.out.println(e.getMessage());
        }
    }

    @HystrixCommand(commandKey = "user")
    public String getUser() {
        randomSleep();
        return "success";
    }

    public String change() {
        ConfigurationManager.getConfigInstance()
                .setProperty("hystrix.command.user.execution.isolation.thread.timeoutInMilliseconds", 3000);
        return "success";
    }
}
