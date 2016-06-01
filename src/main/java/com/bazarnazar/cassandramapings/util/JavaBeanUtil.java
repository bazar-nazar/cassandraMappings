package com.bazarnazar.cassandramapings.util;

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

    public static Field getFieldByAccessor(Method method) throws IntrospectionException,
                                                                 NoSuchFieldException {
        Class<?> clazz = method.getDeclaringClass();
        BeanInfo info = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] props = info.getPropertyDescriptors();
        for (PropertyDescriptor pd : props) {
            if (method.equals(pd.getWriteMethod()) || method.equals(pd.getReadMethod())) {
                return clazz.getDeclaredField(pd.getDisplayName());
            }
        }
        return null;
    }

    public static Method getSetterByField(Field field) throws IntrospectionException {
        return getPropertyDescriptorByField(field).getWriteMethod();
    }

    public static Method getGetterByField(Field field) throws IntrospectionException {
        return getPropertyDescriptorByField(field).getReadMethod();
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
