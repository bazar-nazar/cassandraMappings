package com.bazarnazar.cassandramapings.util;

import com.bazarnazar.cassandramapings.exceptions.QueryException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Created by Bazar on 01.06.16.
 */
public final class CassandraManagerUtil {

    private CassandraManagerUtil() {
    }

    public static <T, R> Function<T, R> fieldToExtractor(Field field, Class<R> fieldClass,
            Class<T> objectClass) {
        //todo isMethods should be supported
        try {
            Method accessor = JavaBeanUtil.getGetterByField(field);
            return t -> {
                try {
                    return (R) accessor.invoke(t);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new QueryException(e);
                }
            };
        } catch (Throwable e) {
            throw new QueryException(e);
        }
    }
}
