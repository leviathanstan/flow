package com.rdc.zrj.flow.hystrixdynamic.config;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.FixedDelayPollingScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leviathanstan
 * @date 05/08/2020 20:12
 */
@Configuration
public class DynamicConfig {

    @Bean
    public DynamicConfiguration dynamicConfiguration(DynamicSource source) {
        DynamicConfiguration configuration = new DynamicConfiguration(
                source, new FixedDelayPollingScheduler(
                        //初始化延迟
                        30 * 1000,
                        //拉取间隔
                        5000,
                        false)
        );
        ConfigurationManager.install(configuration);
        return configuration;
    }
}
