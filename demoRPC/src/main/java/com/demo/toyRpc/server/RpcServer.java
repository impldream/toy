package com.demo.toyRpc.server;

import com.demo.toyRpc.annotation.RpcService;
import com.demo.toyRpc.common.Request;
import com.demo.toyRpc.common.Response;
import com.demo.toyRpc.common.serialize.Decoder;
import com.demo.toyRpc.common.serialize.Encoder;
import com.demo.toyRpc.common.utils.ClassUtil;
import com.demo.toyRpc.common.utils.PropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcServer {
    private String serverAddress;
    private ServiceRegistry serviceRegistry;
    private String servicePackage;

    private Map<String, Object> services = new ConcurrentHashMap<>();

    {
        Properties properties = PropertiesUtil.load("server-config.properties");
        serverAddress = properties.getProperty("server.address");
        servicePackage = properties.getProperty("server.servicePackage");
        serviceRegistry = new ServiceRegistry(properties.getProperty("registry.address"));
        getRpcService();
    }

    private void getRpcService() {
        List<Class<?>> classList = ClassUtil.getClassList(servicePackage);
        if (classList == null || classList.isEmpty()) return;
        for (Class c : classList) {
            if (c.isAnnotationPresent(RpcService.class)) {
                String interfaceName = ((RpcService)c.getAnnotation(RpcService.class)).value().getName();
                try {
                    services.put(interfaceName, c.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String> getActiveServiceName() {
        return new ArrayList<>(services.keySet());
    }

    public void start() {
        //启动Rpc服务, bossGroup用于接收连接，workerGroup用于处理读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new Decoder(Request.class))
                                    .addLast(new Encoder(Response.class))
                                    .addLast(new ServerHandler(services));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelFuture future = bootstrap.bind(host, port).sync();
            log.debug("server started in {} on port {}", host, port);

            if (serviceRegistry != null) {
                // 向注册中心注册服务地址
                serviceRegistry.export(serverAddress);
            }

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
