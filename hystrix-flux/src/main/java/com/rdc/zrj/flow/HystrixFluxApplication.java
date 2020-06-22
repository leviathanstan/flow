package com.rdc.zrj.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

/**
 * @author leviathanstan
 * @date 05/08/2020 14:21
 */
@SpringBootApplication
public class HystrixFluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixFluxApplication.class, args);
    }
}
