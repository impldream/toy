package com.demo.toyRpc.common.serialize;

import com.demo.toyRpc.common.utils.SerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Decoder extends ByteToMessageDecoder {
    private Class<?> clazz;


    //按照编码的格式解码
    // 第一部分：对象序列化数据的长度，起到校验的作用，若接收到的ByteBuf数据长度小于它，则此ByteBuf不对（接收到的信息不对）。
    // 第二部分：序列化数据
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex(); // 标记索引
        int dataLen = byteBuf.readInt();
        if (dataLen < 0) {
            channelHandlerContext.close();

        }

        if (byteBuf.readableBytes() < dataLen) {
            byteBuf.resetReaderIndex(); // 重置索引为标记索引
            return;
        }

        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);
        Object obj = SerializeUtil.deserialize(data, clazz);
        list.add(obj);
    }


}
