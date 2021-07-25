package com.demo.toyRpc.server;

import com.demo.toyRpc.common.Request;
import com.demo.toyRpc.common.Response;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.util.Map;

@Slf4j
public class ServerHandler  extends SimpleChannelInboundHandler<Request> {
    private final Map<String, Object> services;

    public ServerHandler(Map<String, Object> services) {
        this.services = services;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        try {
            //执行服务器端的服务
            Object result = handle(request);
            //设置结果
            response.setResult(result);
        } catch (Throwable t) {
            //设置error
            response.setError(t);
        }
        //返回
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server handler caught exception", cause);
        ctx.close();
    }

    //    使用反射或cglib调用服务
    private Object handle(Request request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = services.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

//        jdk反射执行方法
//        Method method = serviceClass.getMethod(methodName, parameterTypes);
//        method.setAccessible(true);
//        return method.invoke(serviceBean, parameters);

//        cglib调用方法
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }
}
