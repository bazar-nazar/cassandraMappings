package com.bazarnazar.cassandramapings.querybuilder.impl;

import com.bazarnazar.cassandramapings.util.JavaBeanUtil;
import com.bazarnazar.cassandramapings.util.Tuple;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Bazar on 30.05.16.
 */
public class ColumnAccessHandler implements MethodHandler {

    private static final Map<Class<?>, Class<?>> PROXYCLASS_CLASS_MAP = new ConcurrentHashMap<>();

    public static <T> Tuple<T, ColumnAccessHandler> proxyEntity(Class<T> entityClass) throws
                                                                                      InvocationTargetException,
                                                                                      NoSuchMethodException,
                                                                                      InstantiationException,
                                                                                      IllegalAccessException {
        ColumnAccessHandler handler = new ColumnAccessHandler();
        Class<T> proxyClass = (Class<T>) PROXYCLASS_CLASS_MAP
                .computeIfAbsent(entityClass, (e) -> getProxyClass(entityClass));
        T proxy = proxyClass.newInstance();
        ((Proxy) proxy).setHandler(handler);
        return new Tuple<>(proxy, handler);
    }

    private static <T> Class<?> getProxyClass(Class<T> entityClass) {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(entityClass);
        factory.setFilter(method -> true);
        return factory.createClass();
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
