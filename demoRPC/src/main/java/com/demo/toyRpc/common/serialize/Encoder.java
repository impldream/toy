package com.demo.toyRpc.common.serialize;

import com.demo.toyRpc.common.utils.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Encoder extends MessageToByteEncoder {
    private Class<?> clazz;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (clazz.isInstance(o)) {
            byte[] data = SerializeUtil.serialize(o);
            byteBuf.writeInt(data.length); // 指定data长度
            byteBuf.writeBytes(data);
        }
    }
}
