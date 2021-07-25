package com.demo.toyRpc.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class ClassUtil {
    public static List<Class<?>> getClassList(String packageName) {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
            // 从包名获取 URL 类型的资源
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
            // 遍历 URL 资源
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    // 获取协议名（分为 file 与 jar）
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        // 若在 class 目录中，则执行添加类操作
                        String packagePath = url.getPath().replaceAll("%20", " ");
                        addClass(classList, packagePath, packageName);
                    } else if (protocol.equals("jar")) {
                        // 若在 jar 包中，则解析 jar 包中的 entry
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            // 判断该 entry 是否为 class
                            if (jarEntryName.endsWith(".class")) {
                                // 获取类名
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                // 执行添加类操作
                                doAddClass(classList, className);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取类出错！", e);
        }
        return classList;
    }

    private static void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        try {
            // 获取包名路径下的 class 文件或目录
            File[] files = new File(packagePath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
                }
            });
            // 遍历文件或目录
            for (File file : files) {
                String fileName = file.getName();
                // 判断是否为文件或目录
                if (file.isFile()) {
                    // 获取类名

                    String className = fileName.substring(0, fileName.lastIndexOf("."));
                    if (!isEmpty(packageName)) {
                        className = packageName + "." + className;
                    }
                    // 执行添加类操作
                    doAddClass(classList, className);
                } else {
                    // 获取子包
                    String subPackagePath = fileName;
                    if (!isEmpty(packagePath)) {
                        subPackagePath = packagePath + "/" + subPackagePath;
                    }
                    // 子包名
                    String subPackageName = fileName;
                    if (!isEmpty(packageName)) {
                        subPackageName = packageName + "." + subPackageName;
                    }
                    // 递归调用
                    addClass(classList, subPackagePath, subPackageName);
                }
            }
        } catch (Exception e) {
            log.error("添加类出错！", e);
        }
    }

    private static void doAddClass(List<Class<?>> classList, String className) {
        // 加载类
        Class<?> cls;
        try {
            cls = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            log.error("加载类出错！", e);
            throw new RuntimeException(e);
        }
        classList.add(cls);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
