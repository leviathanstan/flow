/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rdc.zrj.flow.sentinelcluster.server;

import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DemoClusterServerInitFunc implements InitFunc {

    private final String remoteAddress = "localhost";
    private final String groupId = "SENTINEL_GROUP";
    private final String namespaceSetDataId = "cluster-server-namespace-set";
    private final String serverTransportDataId = "cluster-server-transport-config";

    @Override
    public void init() throws Exception {
        String flowRulePath = URLDecoder.decode(getClass().getClassLoader().getResource("FlowRule.json").getFile(), "UTF-8");;
        // Register cluster flow rule property supplier which creates data source by namespace.
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            try {
                ReadableDataSource<String, List<FlowRule>> ds = new FileRefreshableDataSource<>(
                        flowRulePath, source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
                return ds.getProperty();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        });
        // Register cluster parameter flow rule property supplier.
//        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
//            ReadableDataSource<String, List<ParamFlowRule>> ds = new NacosDataSource<>(remoteAddress, groupId,
//                namespace + DemoConstants.PARAM_FLOW_POSTFIX,
//                source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {}));
//            return ds.getProperty();
//        });

        // Server namespace set (scope) data source.
        String namePath = URLDecoder.decode(getClass().getClassLoader().getResource("namespace.json").getFile(), "UTF-8");;
        ReadableDataSource<String, Set<String>> namespaceDs = new FileRefreshableDataSource<>(
                namePath, source -> JSON.parseObject(source, new TypeReference<Set<String>>() {}));
        ClusterServerConfigManager.registerNamespaceSetProperty(namespaceDs.getProperty());
//        // Server transport configuration data source.
//        ReadableDataSource<String, ServerTransportConfig> transportConfigDs = new NacosDataSource<>(remoteAddress,
//            groupId, serverTransportDataId,
//            source -> JSON.parseObject(source, new TypeReference<ServerTransportConfig>() {}));
//        ClusterServerConfigManager.registerServerTransportProperty(transportConfigDs.getProperty());
    }
}
