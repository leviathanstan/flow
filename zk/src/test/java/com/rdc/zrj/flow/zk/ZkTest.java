package com.rdc.zrj.flow.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author leviathanstan
 * @date 05/12/2020 18:40
 */
public class ZkTest {

    @Test
    public void zookeeper() throws IOException, KeeperException, InterruptedException {
        // 创建连接
        ZooKeeper zooKeeper = new ZooKeeper(System.getenv("FLOW_ZOOKEEPER"), 5000,
                // 监控所有被触发的事件
                watchedEvent -> System.out.println("Watched " + watchedEvent.getType() + " event!"));

        // 创建节点 - 顺序临时节点
        String path = zooKeeper.create("/root", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        // 判断节点是否存在，并监听-创建、删除、数据更新事件
        Stat stat = zooKeeper.exists(path, true);

        // 获取节点数据
        byte[] data = zooKeeper.getData(path, false, null);

        // 更新节点数据
        stat = zooKeeper.setData(path, "Hello,ZooKeeper".getBytes(), -1);

        // 删除节点
        zooKeeper.delete(path, -1);

        // 获取子节点列表
        List<String> childrenPathList = zooKeeper.getChildren("/", false);
        for (String childrenPath : childrenPathList) {
            System.out.println(childrenPath);
        }

        // 关闭连接
        zooKeeper.close();
    }

    @Test
    public void testLock() throws Exception{
        new Thread(() -> {
            try {
                ZooKeeper zk = new ZooKeeper(System.getenv("FLOW_ZOOKEEPER"), 30000, null);
                Stat stat = zk.exists("/locks", false);
                if(stat == null){
                    // 创建根节点
                    System.out.println("....");
                    zk.create("/locks", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                zk.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(5000);
    }
}
