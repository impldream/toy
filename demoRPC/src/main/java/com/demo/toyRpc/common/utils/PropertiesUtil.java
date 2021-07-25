package com.demo.toyRpc.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {
    public static Properties load(String propsPath) {
        if (propsPath == null || propsPath.length() == 0) {
            throw new IllegalArgumentException();
        }
        Properties props = new Properties();
        InputStream is = null;
        try {
            String suffix = ".properties";
            if (propsPath.lastIndexOf(suffix) == -1) {
                propsPath += suffix;
            }
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsPath);
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            log.error("加载属性文件出错！", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.error("释放资源出错！", e);
            }
        }
        return props;
    }
}
