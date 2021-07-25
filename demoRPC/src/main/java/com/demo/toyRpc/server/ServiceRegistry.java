package com.demo.toyRpc.server;

import com.demo.toyRpc.common.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ServiceRegistry {
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private String registryAddress;

    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void export(String data) throws IOException, InterruptedException, KeeperException {
        if (data == null) return;
        ZooKeeper zk = connect();
        if (zk != null) {
            createNode(zk, data);
        }
    }

    private ZooKeeper connect() throws IOException {
        ZooKeeper zk = new ZooKeeper(registryAddress, Config.ZK_SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
            }
        });
        return zk;
    }

    private void createNode(ZooKeeper zk, String data) throws InterruptedException, KeeperException {
        byte[] bytes = data.getBytes();
        if (zk.exists(Config.ZK_REGISTRY_PATH, true) == null) {
            zk.create(Config.ZK_REGISTRY_PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        String path = zk.create(Config.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        log.debug("create zookeeper node ({} => {})", path, data);
    }
}
