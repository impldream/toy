package com.demo.toyRpc;

import com.demo.toyRpc.client.RpcProxy;
import com.demo.toyRpc.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Consumer {
    @Test
    public void test() throws InterruptedException {
        RpcProxy proxy = new RpcProxy();
        HelloService helloService = proxy.create(HelloService.class);
        String result = helloService.Hello("World");
        log.info(result);
        Thread.sleep(1000);
    }
}
