package com.adins.mss.base.common;

import com.adins.mss.base.util.GsonHelper;
import com.adins.mss.constant.Global;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by adityapurwa on 12/03/15.
 * Used to convert a hash map to object, because Java is cursed with type erasure.
 */
public class LinkedHashMapToObject {
    /**
     * Convert a hash map into object.
     *
     * @param hashMap  The hashmap to convert.
     * @param metadata Object metadata.
     * @param <T>      Object type.
     * @return Object representing the hash map.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     */
    public static <T> T convert(LinkedHashMap hashMap, Class<T> metadata, boolean generateUuid) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Constructor constructor = metadata.getConstructor();
        T instance = (T) constructor.newInstance();
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            String property = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            Method[] methods = instance.getClass().getMethods();
            for (Method m : methods) {
                if (m.getName().toLowerCase().equals("set" + property.toLowerCase())) {
                    Class<?> paramType = m.getParameterTypes()[0];

                    Object castedValue = resolveValue(value, paramType);

                    m.invoke(instance, castedValue);

                    break;
                }
            }
        }

        if (generateUuid) {
            Method uuidSetter = null;
            for (Method m : metadata.getMethods()) {
                if (m.getName().toLowerCase().equals("setuuid_" + metadata.getSimpleName().toLowerCase())) {
                    uuidSetter = m;
                    break;
                }
            }
            if (uuidSetter != null) {
                uuidSetter.invoke(instance, UUID.randomUUID().toString());
            }
        }
        return instance;
    }

    /**
     * Resolve a value based on the type.
     *
     * @param value     Value to resolve.
     * @param paramType Type of the value.
     * @return Casted value, or original value if the value type is not recognized.
     */
    private static Object resolveValue(String value, Class<?> paramType) {

        Object castedValue = value;
        try {
            if (paramType.getSimpleName().equals("Date")) {
                castedValue = GsonHelper.fromJson(value, Date.class);
            }
            if (paramType.getSimpleName().equals("Integer")) {
                castedValue = GsonHelper.fromJson(value, Integer.class);
            }
            if (paramType.getSimpleName().equals("Long")) {
                castedValue = GsonHelper.fromJson(value, Long.class);
            }
            if (paramType.getSimpleName().equals("String")) {
                castedValue = GsonHelper.fromJson(value, String.class);
            }
        } catch (Exception ex) {
            if (Global.IS_DEV)
                ex.printStackTrace();
        }
        return castedValue;
    }

    /**
     * Convert hash map list into list of object that represents it.
     *
     * @param <T>          Object type.
     * @param hashMapList  List of hash map.
     * @param metadata     Object metadata.
     * @param generateUuid
     * @return List of object that represent the list of hash map.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     */
    public static <T> List<T> convert(List<LinkedHashMap> hashMapList, Class<T> metadata, boolean generateUuid) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {

        List<T> converted = new ArrayList<T>();
        for (LinkedHashMap hashMap : hashMapList) {
            T dummyItem = convert(hashMap, metadata, generateUuid);
            converted.add(dummyItem);
        }
        return converted;
    }
}
