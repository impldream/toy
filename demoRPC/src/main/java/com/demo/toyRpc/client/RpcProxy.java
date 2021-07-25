package com.demo.toyRpc.client;

import com.demo.toyRpc.common.Request;
import com.demo.toyRpc.common.Response;
import com.demo.toyRpc.common.utils.PropertiesUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;
import java.util.UUID;

public class RpcProxy {

    //服务提供方地址，由ServiceDiscover组件中获得。
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    {
        Properties properties = PropertiesUtil.load("client-config.properties");
        serviceDiscovery = new ServiceDiscovery(properties.getProperty("discovery.address"));
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Request request = new Request(); // 创建并初始化 RPC 请求
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        if (serviceDiscovery != null) {
                            serverAddress = serviceDiscovery.discover();
                        }

                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);

                        RpcClient client = new RpcClient(host, port);
                        Response response = client.send(request);
                        if (response.isError()) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }
                    }
                }
        );
    }
}
