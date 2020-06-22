package com.rdc.zrj.flow.zk;

import com.rdc.zrj.flow.zk.config.ZkConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leviathanstan
 * @date 05/12/2020 18:55
 */
@SpringBootApplication
@RestController
public class ZkApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZkApplication.class, args);
    }

    @Autowired
    ZkConfiguration zkConfiguration;

    @GetMapping(value = "/get")
    public String get() {
        return zkConfiguration.get();
    }
}
