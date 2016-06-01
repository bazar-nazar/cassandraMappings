package com.bazarnazar.cassandramapings.util;

import com.bazarnazar.cassandramapings.context.pojo.IPojo;
import com.bazarnazar.cassandramapings.exceptions.QueryException;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

/**
 * Created by Bazar on 01.06.16.
 */
public final class CassandraManagerUtil {

    private CassandraManagerUtil() {
    }

    private static final MapperFactory MAPPER_FACTORY = new DefaultMapperFactory.Builder().build();

    public static <T> T proxyEntity(T entity) throws InvocationTargetException,
                                                     NoSuchMethodException, InstantiationException,
                                                     IllegalAccessException {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(entity.getClass());
        factory.setInterfaces(new Class[]{IPojo.class});
        factory.setFilter(method -> Modifier.isAbstract(method.getModifiers()));
        T proxy = (T) factory.create(new Class<?>[0], new Object[0], new ProxyHandler(entity));
        MAPPER_FACTORY.getMapperFacade((Class<T>) entity.getClass(), (Class<T>) entity.getClass())
                      .map(entity, proxy);
        return proxy;
    }

    private static class ProxyHandler implements MethodHandler {

        private Object entity;

        public ProxyHandler(Object entity) {
            this.entity = entity;
        }

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws
                                                                                            Throwable {
            return entity;
        }
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
