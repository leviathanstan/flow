package com.rdc.zrj.flow.hystrixdynamic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

/**
 * @author leviathanstan
 * @date 05/08/2020 19:58
 */
@SpringBootApplication
@EnableCircuitBreaker
public class HystrixDynamicApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixDynamicApplication.class, args);
    }

}
