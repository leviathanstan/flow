package com.rdc.zrj.flow.zk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author leviathanstan
 * @date 05/12/2020 18:55
 */
@Configuration
@RefreshScope
public class ZkConfiguration {

    @Value("${text}")
    public String name;


    public String get() {
        return name;
    }

}
