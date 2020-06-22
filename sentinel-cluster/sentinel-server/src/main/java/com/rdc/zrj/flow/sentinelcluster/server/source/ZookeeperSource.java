package com.rdc.zrj.flow.sentinelcluster.server.source;

import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * @author leviathanstan
 * @date 05/13/2020 11:17
 */
public class ZookeeperSource implements InitFunc {

    private String remoteAddress = System.getenv("FLOW_ZOOKEEPER");
    private String group = "/sentinel/dynamic/";
    private String data = "sentinel-client";

    @Override
    public void init() {
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            return new ZookeeperDataSource<>(remoteAddress, group + data,
                    source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {})
            ).getProperty();
        });
    }
}
