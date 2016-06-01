package com.bazarnazar.cassandramapings.context.pojo;

import com.bazarnazar.cassandramapings.util.JavaBeanUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Bazar on 01.06.16.
 */
public interface IPojo {

    Object getStoredObject();

    default Stream<String> getModifiedColumns() {
        return Arrays.stream(getStoredObject().getClass().getDeclaredFields())
                     .filter(f -> !"storedObject".equals(f.getName()))
                     .map(JavaBeanUtil::getGetterByField).filter(this::changed)
                     .map(JavaBeanUtil::getFieldByAccessor).map(Field::getName);
    }

    default boolean changed(Method method) {
        try {
            if (method.invoke(getStoredObject()) == null) {
                if (method.invoke(this) == null) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return !method.invoke(getStoredObject()).equals(method.invoke(this));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
