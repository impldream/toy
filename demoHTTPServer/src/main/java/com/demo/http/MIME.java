package com.demo.http;

import com.alibaba.fastjson.JSONObject;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class MIME {
    private static final JSONObject jsonObject;

    private static final String JSON_PATH = "static/meta/content-type.json";

    static {
            jsonObject = JSONObject.parseObject(readFile());
    }

    private static String readFile() {
        try {
            RandomAccessFile raf = new RandomAccessFile(JSON_PATH, "r");
            FileChannel channel = raf.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            buffer.clear();
            channel.read(buffer);
            buffer.flip();
            return new String(buffer.array());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static String getContentType(String ext) {
        return jsonObject.getString(ext);
    }
}
