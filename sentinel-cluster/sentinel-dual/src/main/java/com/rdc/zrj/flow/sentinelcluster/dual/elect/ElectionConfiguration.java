package com.rdc.zrj.flow.sentinelcluster.dual.elect;

import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.entity.ClusterClientStateEntity;
import com.alibaba.csp.sentinel.init.InitExecutor;
import com.alibaba.csp.sentinel.transport.command.SimpleHttpCommandCenter;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author leviathanstan
 * @date 05/13/2020 19:42
 */
@Configuration
@Slf4j
public class ElectionConfiguration implements InitializingBean, DisposableBean {

    /**
     * 选举节点
     */
    private static final String PATH = "/sentinel/leader";

    /**
     * SimpleHttpCommandCenter设置client、server模式api
     */
    private static final String MODIFY_CLUSTER_MODE_PATH = "setClusterMode";
    /**
     * SimpleHttpCommandCenter修改client、server模式api
     */
    private static final String MODIFY_CONFIG_PATH = "cluster/client/modifyConfig";
    /**
     * TokenServer自动选举存储当前TokenServer的IP的路径(临时节点)
     */
    private static final String TOKEN_SERVER_IP_PATH = "/sentinel/tokenServerIp";
    private static final String ZOO_URL = "";

    @Autowired
    public CuratorFramework client;

    @Bean
    public CuratorFramework getClient() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(ZOO_URL,
                new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();
        return curatorFramework;
    }

    /**
     * 选举
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception{
        LeaderLatch latch = new LeaderLatch(client, PATH, "Client #" + new Random().nextInt(10000));
        latch.addListener(new LeaderLatchListener() {
            @Override
            public void notLeader() {
                log.info("from leader to follower");
                setClusterMode(0);
                modifyClientConfig();
            }

            @Override
            public void isLeader() {
                log.info("becomes leader");
                setClusterMode(1);
            }
        });
        //需要初始化CommandHandler，供后续server client切换用
        InitExecutor.doInit();
        latch.start();
        latch.await(1, TimeUnit.SECONDS);
        if (!latch.hasLeadership()) {
            setClusterMode(0);
            modifyClientConfig();
            log.info("init to follower");
        }
    }

    @Override
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * 客户端、服务端状态切换
     * @param mode 0:client，1:server
     */
    private void setClusterMode(int mode) {
        CommandRequest request = new CommandRequest();
        request.addParam("mode", String.valueOf(mode));
        try {
            if (client.checkExists().forPath(TOKEN_SERVER_IP_PATH) == null) {
                if (mode == 0) {
                    //没有token server的信息
                    log.error("token server does not exist");
                    return;
                } else if (mode == 1) {
                    //创建一个临时节点来保存server的ip:port
                    client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(TOKEN_SERVER_IP_PATH,
                            (HostNameUtil.getIp() + ":" + ClusterServerConfigManager.getPort()).getBytes());
                }
            }
        } catch (Exception e) {
            log.error("set cluster mode error", e);
            return;
        }
        @SuppressWarnings("unchecked")
        CommandHandler<String> commandHandler = SimpleHttpCommandCenter.getHandler(MODIFY_CLUSTER_MODE_PATH);
        CommandResponse<?> response = commandHandler.handle(request);
        if (!response.isSuccess()) {
            log.error(response.getResult().toString(), response.getException());
        }
    }

    /**
     * 客户端加入server
     */
    private void modifyClientConfig() {
        CommandRequest request = new CommandRequest();
        byte[] bytes;
        ClusterClientStateEntity entity = new ClusterClientStateEntity();
        try {
            if (client.checkExists().forPath(TOKEN_SERVER_IP_PATH) == null) {
                log.error("token server does not exist");
                return;
            }
            //获取TokenServer的ip+port
            bytes = client.getData().forPath(TOKEN_SERVER_IP_PATH);
            if (bytes == null || bytes.length == 0) {
                log.error("the server info is empty");
                return;
            }
            String serverIp = new String(bytes);
            //ip:port
            String[] info = serverIp.split(":");
            entity.setServerHost(info[0]);
            entity.setServerPort(Integer.valueOf(info[1]));
            entity.setRequestTimeout(60);
            request.addParam("data", JSON.toJSONString(entity));
            CommandHandler<?> commandHandler = SimpleHttpCommandCenter.getHandler(MODIFY_CONFIG_PATH);
            CommandResponse<?> response = commandHandler.handle(request);
            if (!response.isSuccess()) {
                log.error(response.getResult().toString(), response.getException());
            }
        } catch (Exception e) {
            log.error("modify client config error", e);
        }
    }
}
