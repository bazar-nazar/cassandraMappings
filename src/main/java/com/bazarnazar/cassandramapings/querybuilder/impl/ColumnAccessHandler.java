package com.bazarnazar.cassandramapings.querybuilder.impl;

import com.bazarnazar.cassandramapings.util.JavaBeanUtil;
import com.bazarnazar.cassandramapings.util.Tuple;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Bazar on 30.05.16.
 */
public class ColumnAccessHandler implements MethodHandler {

    private static final ConcurrentHashMap<Class<?>, Class<?>> PROXY_CLASS_MAP = new
            ConcurrentHashMap<>();

        public static <T> Tuple<T, ColumnAccessHandler> proxyEntity(Class<T> entityClass) throws
                                                                                          InvocationTargetException,
                                                                                          NoSuchMethodException,
                                                                                          InstantiationException,
                                                                                          IllegalAccessException {
            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(entityClass);
            factory.setFilter(method -> true);
            ColumnAccessHandler handler = new ColumnAccessHandler();
            T proxy = (T) factory.create(new Class<?>[0], new Object[0], handler);
            return new Tuple<>(proxy, handler);
        }

    private Field lastAccessedColumn = null;

    public ColumnAccessHandler() {
    }

    public Field getLastAccessedColumnField() {
        return lastAccessedColumn;
    }

    @Override
    public Object invoke(Object o, Method method, Method proceed, Object[] objects) throws
                                                                                    Throwable {
        lastAccessedColumn = JavaBeanUtil.getFieldByAccessor(method);
        return null;
    }
}
