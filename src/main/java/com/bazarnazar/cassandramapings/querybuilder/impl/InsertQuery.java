package com.bazarnazar.cassandramapings.querybuilder.impl;

import com.bazarnazar.cassandramapings.annotations.Generated;
import com.bazarnazar.cassandramapings.exceptions.QueryBuilderException;
import com.bazarnazar.cassandramapings.util.EntityDefinitionUtil;
import com.bazarnazar.cassandramapings.util.JavaBeanUtil;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Transient;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by Bazar on 02.06.16.
 */
public class InsertQuery<T> {

    T entity;

    InsertQuery(T entity) {
        this.entity = entity;
    }

    public Statement build() {
        Table table;
        if ((table = entity.getClass().getDeclaredAnnotation(Table.class)) == null) {
            throw new QueryBuilderException(
                    "Cant create query builder: " + entity.getClass().getName() + " " +
                            "doesn't have @Table annotation");
        }
        String tableName = table.name();
        Insert insert = QueryBuilder.insertInto(tableName);
        Arrays.stream(entity.getClass().getDeclaredFields())
              .filter(f -> f.getDeclaredAnnotation(Transient.class) == null)
              .forEach(f -> addValue(f, insert));
        return insert;
    }

    private void addValue(Field f, Insert insert) {
        Method extractor = JavaBeanUtil.getGetterByField(f);
        try {
            if (f.getDeclaredAnnotation(Generated.class) != null && extractor
                    .invoke(entity) == null) {
                insert.value(EntityDefinitionUtil.getColumnName(f),
                             f.getDeclaredAnnotation(Generated.class).value().getOperation());
            } else {
                insert.value(EntityDefinitionUtil.getColumnName(f), extractor.invoke(entity));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new QueryBuilderException(e);
        }
    }

}
