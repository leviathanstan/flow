package com.rdc.zrj.flow.sentinelcluster.server;

import com.alibaba.csp.sentinel.cluster.server.ClusterTokenServer;
import com.alibaba.csp.sentinel.cluster.server.SentinelDefaultTokenServer;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;

import java.util.Collections;

/**
 * @author leviathanstan
 * @date 05/08/2020 15:59
 */
public class SentinelServerApplication {

    public static void main(String[] args) throws Exception {
        // Not embedded mode by default (alone mode).
        ClusterTokenServer tokenServer = new SentinelDefaultTokenServer();

        // A sample for manually load config for cluster server.
        // It's recommended to use dynamic data source to cluster manage config and rules.
        // See the sample in DemoClusterServerInitFunc for detail.
        ClusterServerConfigManager.loadGlobalTransportConfig(new ServerTransportConfig()
                .setIdleSeconds(600)
                .setPort(11111));
        ClusterServerConfigManager.loadServerNamespaceSet(Collections.singleton("sentinel-client"));
        ClusterServerConfigManager.loadGlobalFlowConfig(new ServerFlowConfig().setMaxAllowedQps(1000));

        // Start the server.
        tokenServer.start();
    }
}
