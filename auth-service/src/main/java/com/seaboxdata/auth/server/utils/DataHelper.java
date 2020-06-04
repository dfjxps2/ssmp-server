package com.seaboxdata.auth.server.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author makaiyu
 * @date 2019/5/21 10:08
 */
@Slf4j
public class DataHelper {

    public static final String SET_METHOD = "set";

    public static final String GET_METHOD = "get";

    public static void putDataIntoEntity(Map<String, Object> map, Object entity) throws SecurityException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (map != null && map.size() > 0) {
            //获取传入实体类的属性Filed数组
            Field[] fieldArr = Class.forName(entity.getClass().getCanonicalName()).getDeclaredFields();
            //遍历数组
            for (Field field : fieldArr) {
                //获取属性名称
                String fieldName = field.getName();
                //判断map中是否存在对应的属性名称（注：这个方法要想使用就必须保证map中的key与实体类的属性名称一致）
                Map<String, Object> humpName = formatHumpName(map);
                if (humpName.containsKey(fieldName)) {
                    //调用本类中的帮助方法来获取当前属性名对应的方法名（“set”为getMethodName方法的第二个参数）
                    String methodName = getMethodName(fieldName, SET_METHOD);
                    //获取当前key对应的值
                    Object obj = humpName.get(fieldName);
                    //根据获取的方法名称及当前field的类型获取method对象
                    Method method = entity.getClass().getDeclaredMethod(methodName, field.getType());
                    //调用当前实体类的方法将数值set进去
                    method.invoke(entity, obj);
                }
            }
        }
    }

    /**
     * @param key        属性名
     * @param MethodType 获取方法类型（set or get）
     * @return 方法名称，反射使用
     * @author Chunf
     */
    public static String getMethodName(String key, String MethodType) {
        String methodName = "";
        if (key != null && !"".equals(key)) {
            String[] arr = key.split("");
            for (int i = 0; i < arr.length; i++) {
                String temp = arr[i];
                if (i == 0) {
                    methodName += temp.toUpperCase();
                } else {
                    methodName += temp;
                }
            }
        }
        return MethodType + methodName;
    }

    public static Map<String, Object> formatHumpName(Map<String, Object> map) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            String newKey = toFormatCol(key);
            newMap.put(newKey, entry.getValue());
        }
        return newMap;
    }

    public static String toFormatCol(String colName) {
        StringBuilder sb = new StringBuilder();
        String[] str = colName.toLowerCase().split("_");
        int i = 0;
        for (String s : str) {
            if (s.length() == 1) {
                s = s.toUpperCase();
            }
            i++;
            if (i == 1) {
                sb.append(s);
                continue;
            }
            if (s.length() > 0) {
                sb.append(s.substring(0, 1).toUpperCase());
                sb.append(s.substring(1));
            }
        }
        return sb.toString();
    }
}
