package com.bazarnazar.cassandramapings.querybuilder.impl;

import com.bazarnazar.cassandramapings.querybuilder.IQueryBuilder;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQuery;
import com.bazarnazar.cassandramapings.util.QueryUtil;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Bazar on 02.06.16.
 */
public class SafeDeleteBuilder<T> implements IQueryBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SafeDeleteBuilder.class);

    private Class<T> entityClass;
    private ISafeSelectQuery<T> safeSelectQuery;

    SafeDeleteBuilder(Class<T> entityClass, ISafeSelectQuery<T> safeSelectQuery) {
        this.entityClass = entityClass;
        this.safeSelectQuery = safeSelectQuery;
    }

    @Override
    public Statement build() {
        String tableName = QueryUtil.getTableName(entityClass);
        Delete from = QueryBuilder.delete().from(tableName);
        safeSelectQuery.setWhere(from::where);
        LOGGER.debug("Delete: {}", from.toString());
        return from;
    }

}
