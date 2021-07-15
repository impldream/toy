package com.demoJson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

public class GenerateJava {

    public static void process(String text) throws IOException {
        JSONObject jsonObject = JSONObject.load(text);
        process(jsonObject);
    }

    public static void process(JSONObject jsonObject) {
        process(jsonObject,"./JavaBean.java");
    }

    public static void process(JSONObject jsonObject, String path) {
        if (!path.endsWith(".java")) {
            throw new JSONException("must be java file");
        }
        File file = new File(path);
        String className = path.substring(path.lastIndexOf('/')+1, path.length()-5);
        try {
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write("public class " + className + " {\n");

            Queue<String[]> queue = new ArrayDeque<>();

            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                Object type = entry.getValue();
                String key = entry.getKey();

                String clazz = key.substring(0, 1).toUpperCase() + key.substring(1);
                String obj = key.substring(0,1).toLowerCase() + key.substring(1);

                StringBuilder sb = new StringBuilder();
                String returnType = getType(type, clazz, type instanceof JSONArray);
                sb.append('\t').append(returnType).append(' ').append(obj).append(";\n");
                queue.offer(new String[]{returnType, clazz, obj});

                fileWriter.write(sb.toString());
            }
            String getSet = genSetGet(queue);

            fileWriter.write(getSet);
            fileWriter.write("\n}");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getType(Object element, String clazz, boolean isArray) {
        String type = "";
        if (element instanceof JSONObject) {
            type = clazz;
            process((JSONObject) element, "./" + clazz + ".java");
        } else if (element instanceof JSONArray) {
            if (!((JSONArray) element).isEmpty())
                return "List<" + getType(((JSONArray) element).get(0), clazz, true) + ">";
        } else if (element instanceof Double) {
            type = isArray ? "Double" : "double";
        } else if (element instanceof Float) {
            type = isArray ? "Float" : "float";
        } else if (element instanceof Long) {
            type = isArray ? "Long" : "long";
        } else if (element instanceof Integer) {
            type = isArray ? "Integer" : "int";
        } else if (element instanceof String) {
            type = "String";
        } else if (element instanceof Boolean) {
            type = isArray ? "Boolean" : "boolean";
        }
        return type;
    }


    private static String genSetGet(Queue<String[]> queue) {
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            String[] pair = queue.poll();
            sb.append("\n\tpublic void set").append(pair[1]).append('(')
                    .append(pair[0]).append(' ').append(pair[2]).append(") {\n\t\tthis.")
                    .append(pair[2]).append(" = ").append(pair[2]).append(";\n\t}\n");

            sb.append("\n\tpublic ").append(pair[0]).append(" get").append(pair[1]).append("() {\n\t\treturn ")
            .append(pair[2]).append(";\n\t}\n");
        }

        return sb.toString();
    }
}
