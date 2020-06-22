package com.rdc.zrj.flow.sentinelcluster.client.datasource;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * @author leviathanstan
 * @date 05/13/2020 11:01
 */
public class InitZookeeperSource implements InitFunc {

    private String remoteAddress = System.getenv("FLOW_ZOOKEEPER");
    private String group = "/sentinel/dynamic/";
    private String data = "sentinel-client";

    @Override
    public void init() {
        initFlow();
    }

    private void initFlow() {
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new ZookeeperDataSource<>(remoteAddress, group + data,
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }
}
