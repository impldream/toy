package com.demo.toyRpc.service.impl;

import com.demo.toyRpc.annotation.RpcService;
import com.demo.toyRpc.service.HelloService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String Hello(String message) {
        String result = "Hello " + message;
        System.out.println(result);
        return result;
    }
}
