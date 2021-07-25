package com.demo.toyRpc;

import com.demo.toyRpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

@Slf4j
public class Producer {
    @Test
    public void test() {
       // BasicConfigurator.configure();
        RpcServer server = new RpcServer();
        log.info("all services: {}", server.getActiveServiceName());
        server.start();
    }
}
