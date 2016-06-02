package com.bazarnazar.cassandramapings.util;

import com.bazarnazar.cassandramapings.context.pojo.IPojo;
import com.bazarnazar.cassandramapings.exceptions.QueryException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Bazar on 01.06.16.
 */
public final class JavaBeanUtil {

    private JavaBeanUtil() {
    }

    public static Field getFieldByAccessor(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        BeanInfo info = null;
        try {
            info = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (method.equals(pd.getWriteMethod()) || method.equals(pd.getReadMethod())) {
                    return clazz.getDeclaredField(pd.getDisplayName());
                }
            }
            return null;
        } catch (IntrospectionException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getSetterByField(Field field) {
        try {
            return getPropertyDescriptorByField(field).getWriteMethod();
        } catch (IntrospectionException e) {
            throw new RuntimeException();
        }
    }

    public static Method getGetterByField(Field field) {
        try {
            return getPropertyDescriptorByField(field).getReadMethod();
        } catch (IntrospectionException e) {
            throw new QueryException(e);
        }
    }

    private static PropertyDescriptor getPropertyDescriptorByField(Field field) throws
                                                                                IntrospectionException {
        Class<?> clazz = field.getDeclaringClass();
        BeanInfo info = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] props = info.getPropertyDescriptors();
        return Arrays.stream(props).filter(p -> field.getName().equals(p.getDisplayName()))
                     .findAny().get();
    }

}
