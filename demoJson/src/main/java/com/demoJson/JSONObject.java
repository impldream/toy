package com.demoJson;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

public class JSONObject extends JSON implements Map<String, Object>, Cloneable, Serializable, InvocationHandler {
    private static final int          DEFAULT_INITIAL_CAPACITY = 16;

    public final Map<String, Object> map;

    public JSONObject(){
        this(DEFAULT_INITIAL_CAPACITY, false);
    }

    public JSONObject(Map<String, Object> map){
        if (map == null) {
            throw new IllegalArgumentException("map is null.");
        }
        this.map = map;
    }

    public JSONObject(boolean ordered){
        this(DEFAULT_INITIAL_CAPACITY, ordered);
    }

    public JSONObject(int initialCapacity){
        this(initialCapacity, false);
    }

    public JSONObject(int initialCapacity, boolean ordered){
        if (ordered) {
            map = new LinkedHashMap<String, Object>(initialCapacity);
        } else {
            map = new HashMap<String, Object>(initialCapacity);
        }
    }

    public static JSONObject load(String text) throws IOException {
        File file = new File(text);

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder json = new StringBuilder();
        while (bufferedReader.ready()) {
            json.append(bufferedReader.readLine());
        }
        return toJSON(json.toString());
    }

    public static void save(JSONObject text, String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(text.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public static JSONObject toJSON(String text) {
        return (JSONObject)JSON.parse(new StringBuilder(text));
    }
    // TODO
    public JSONObject getJSONObject(String key) {
        Object value = map.get(key);

        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }

        if (value instanceof Map) {
            return new JSONObject((Map) value);
        }

        if (value instanceof String) {
            return JSON.parseObject((StringBuilder) value);
        }

        return null;
    }

    public JSONArray getJSONArray(String key) {
        Object value = map.get(key);
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }

        if (value instanceof List) {
            return new JSONArray((List)value);
        }

        if (value instanceof String) {
            return (JSONArray) JSON.parse(new StringBuilder((String)value));
        }
        return null;
    }

    public String getString(String key) {
        Object obj = map.get(key);

        return (String)obj;
    }

    public Double getDouble(String key) {
        Object obj = map.get(key);
        return (Double) obj;
    }

    public Float getFloat(String key) {
        return getDouble(key).floatValue();
    }

    public Long getLong(String key) {
        Object obj = map.get(key);
        return (Long) obj;
    }

    public Boolean getBoolean(String key) {
        Object obj = map.get(key);
        return (Boolean) obj;
    }

    public Integer getInteger(String key) {
        return getLong(key).intValue();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("not support now!!!");
        return null;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        boolean result = map.containsKey(key);
        if (!result) {
            if (key instanceof Number
                    || key instanceof Character
                    || key instanceof Boolean
                    || key instanceof UUID
            ) {
                result = map.containsKey(key.toString());
            }
        }
        return result;
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        Object val = map.get(key);

        if (val == null) {
            if (key instanceof Number
                    || key instanceof Character
                    || key instanceof Boolean
                    || key instanceof UUID
            ) {
                val = map.get(key.toString());
            }
        }

        return val;
    }

    @Override
    public Object put(String key, Object value) {
        if (value instanceof Boolean) {
            map.put(key, (Boolean)value);
        } else if (value instanceof Long) {
            map.put(key, (Long)value);
        } else if (value instanceof Double) {
            map.put(key, (Double)value);
        } else if (value instanceof String) {
            map.put(key, (String)value);
        } else if (value instanceof JSONArray) {
            map.put(key, (JSONArray)value);
        } else if (value instanceof JSONObject) {
            map.put(key, (JSONObject)value);
        }
        return map;
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\"");
            json.append(":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            json.append(",");
        }
        if (json.length() > 1)
            json.replace(json.length()-1, json.length(), "}");
        else
            json.append('}');
        return json.toString();
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof JSONObject) {
            if (this == anObject)
                return true;
            JSONObject json = (JSONObject) anObject;
            for (Map.Entry<String, Object> entry : this.map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (!json.containsKey(key)) {
                    return false;
                }
                if (!json.get(key).equals(value)) {
                    return false;
                }
            }

            for (Map.Entry<String, Object> entry : json.map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (!map.containsKey(key)) {
                    return false;
                }
                if (!map.get(key).equals(value)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
