package com.bazarnazar.cassandramapings.util;

import com.bazarnazar.cassandramapings.annotations.Generated;
import com.bazarnazar.cassandramapings.context.pojo.IPojo;
import com.bazarnazar.cassandramapings.exceptions.QueryBuilderException;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Created by Bazar on 02.06.16.
 */
public final class QueryUtil {

    private QueryUtil() {
    }

    public static <T> String getTableName(Class<T> entity) {
        Table table;
        if ((table = entity.getDeclaredAnnotation(Table.class)) == null && (table = entity
                .getSuperclass().getDeclaredAnnotation(Table.class)) == null) {
            throw new QueryBuilderException(
                    "Cant create query builder: " + entity.getClass().getName() + " " +
                            "doesn't have @Table annotation");
        }
        return table.name();
    }

    public static <T extends D, D> Class<D> getEntityClass(T entity) {
        return entity instanceof IPojo ? (Class<D>) entity.getClass()
                                                          .getSuperclass() : (Class<D>) entity
                .getClass();
    }

    public static <T> void setValues(T entity, BiConsumer<String, Object> valueConsumer) {
        setValues(entity, valueConsumer, field -> true);
    }

    public static <T> void setValues(T entity, BiConsumer<String, Object> valueConsumer,
            Predicate<Field> predicate) {
        Class<?> entityClass = entity.getClass();
        if (entity instanceof IPojo) {
            entityClass = entity.getClass().getSuperclass();
        }
        Arrays.stream(entityClass.getDeclaredFields())
              .filter(f -> f.getDeclaredAnnotation(Transient.class) == null).filter(predicate)
              .forEach(f -> addValue(entity, f, valueConsumer));
    }

    private static <T> void addValue(T entity, Field f, BiConsumer<String, Object> valueConsumer) {
        Method extractor = JavaBeanUtil.getGetterByField(f);
        try {
            if (f.getDeclaredAnnotation(Generated.class) != null && extractor
                    .invoke(entity) == null) {
                valueConsumer.accept(EntityDefinitionUtil.getColumnName(f),
                                     f.getDeclaredAnnotation(Generated.class).value()
                                      .getOperation());
            } else {
                valueConsumer
                        .accept(EntityDefinitionUtil.getColumnName(f), extractor.invoke(entity));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new QueryBuilderException(e);
        }
    }

}
