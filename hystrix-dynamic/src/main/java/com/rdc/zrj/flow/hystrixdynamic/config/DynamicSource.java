package com.rdc.zrj.flow.hystrixdynamic.config;

import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leviathanstan
 * @date 05/08/2020 20:13
 */
@Component
public class DynamicSource implements PolledConfigurationSource{
    @Override
    public PollResult poll(boolean initial, Object checkPoint) {
        Map<String, Object> complete = new HashMap<>(8);
        //拉取配置逻辑
        System.out.println(">>>>>begin pull");
        complete.put("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", "3000");
        return PollResult.createFull(complete);
    }
}
