package com.bazarnazar.cassandramapings.querybuilder.impl;

import com.bazarnazar.cassandramapings.querybuilder.IQueryBuilder;
import com.bazarnazar.cassandramapings.util.QueryUtil;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Bazar on 02.06.16.
 */
public class SafeInsertBuilder<T> implements IQueryBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SafeInsertBuilder.class);
    T entity;

    SafeInsertBuilder(T entity) {
        this.entity = entity;
    }

    @Override
    public Statement build() {
        String tableName = QueryUtil.getTableName(entity.getClass());
        Insert insert = QueryBuilder.insertInto(tableName);
        QueryUtil.setValues(entity, insert::value);
        LOGGER.debug("Created insert statement: {}", insert.toString());
        return insert;
    }


}
