package com.demoJson;

import java.io.*;
import java.util.*;

public class JSONArray extends JSON implements List<Object>, Cloneable, RandomAccess, Serializable {
    private final List<Object> list;

    public JSONArray(){
        this.list = new ArrayList<Object>();
    }

    public JSONArray(List<Object> list){
        if (list == null){
            throw new IllegalArgumentException("list is null.");
        }
        this.list = list;
    }

    public JSONArray(int initialCapacity){
        this.list = new ArrayList<Object>(initialCapacity);
    }

    public JSONArray getJSONArray(int index) {
        Object value = list.get(index);

        if (value instanceof JSONArray) {
            return (JSONArray) value;
        } else if (value instanceof List) {
            return new JSONArray((List)value);
        }
        return null;
    }

    public Object getJSONObject(int index) {
        Object value = list.get(index);

        if (value instanceof JSONObject) {
            return (JSONObject)value;
        } else if (value instanceof Map) {
            return new JSONObject((Map)value);
        }
        return null;
    }

    public Boolean getBoolean(int index) {
        Object object = list.get(index);
        return (Boolean) object;
    }

    public Double getDouble(int index) {
        Object object = list.get(index);
        return (Double) object;
    }

    public Float getFloat(int index) {
        return getDouble(index).floatValue();
    }

    public Long getLong(int index) {
        Object object = list.get(index);
        return (Long) object;
    }

    public Integer getInteger(int index) {
        return getLong(index).intValue();
    }

    public static JSONArray toJSON(String text) {
        return (JSONArray) JSON.parse(new StringBuilder(text));
    }

    public static JSONArray load(String text) throws IOException {
        File file = new File(text);

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder array = new StringBuilder();
        while (bufferedReader.ready()) {
            array.append(bufferedReader.readLine());
        }
        return toJSON(array.toString());
    }

    public static void save(JSONArray text, String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(text.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(Object o) {
        return list.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<?> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Object get(int index) {
        return list.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        if (element instanceof Double) {
            list.set(index, (Double)element);
        } else if (element instanceof Long) {
            list.set(index, (Long)element);
        } else if (element instanceof Boolean) {
            list.set(index, (Boolean)element);
        } else if (element instanceof String) {
            list.set(index, (String)element);
        } else if (element instanceof JSONArray) {
            list.set(index, (JSONArray)element);
        } else if (element instanceof JSONObject) {
            list.set(index, (JSONObject)element);
        }
        return list;
    }

    @Override
    public void add(int index, Object element) {
        if (element instanceof Double) {
            list.add(index, (Double)element);
        } else if (element instanceof Long) {
            list.add(index, (Long)element);
        } else if (element instanceof Boolean) {
            list.add(index, (Boolean)element);
        } else if (element instanceof String) {
            list.add(index, (String)element);
        } else if (element instanceof JSONArray) {
            list.add(index, (JSONArray)element);
        } else if (element instanceof JSONObject) {
            list.add(index, (JSONObject)element);
        }
    }

    @Override
    public Object remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return list.toString();
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof JSONArray) {
            if (this == anObject)
                return true;
            JSONArray json = (JSONArray) anObject;
            int len = list.size();
            if (len != json.list.size()) return false;
            int[] visited = new int[len];
            int[] visited1 = new int[len];
            int flag = 0, flag1 = 0;
            for (Object obj : list) {
                for (int i = 0; i < len; i++) {
                    if (visited[i] == 0 && json.list.get(i).equals(obj)) {
                        visited[i] = 1;
                        flag++;
                        break;
                    }
                }
            }

            for (Object obj : json.list) {
                for (int i = 0; i < len; i++) {
                    if (visited1[i] == 0 && list.get(i).equals(obj)) {
                        visited1[i] = 1;
                        flag1++;
                        break;
                    }
                }
            }

            return flag == flag1 && flag == len;
        }
        return false;
    }
}
