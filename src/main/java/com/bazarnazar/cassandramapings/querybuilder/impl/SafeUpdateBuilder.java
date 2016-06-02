package com.bazarnazar.cassandramapings.querybuilder.impl;

import com.bazarnazar.cassandramapings.querybuilder.IQueryBuilder;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQuery;
import com.bazarnazar.cassandramapings.util.QueryUtil;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Bazar on 02.06.16.
 */
public class SafeUpdateBuilder<T> implements IQueryBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SafeUpdateBuilder.class);

    private ISafeSelectQuery<T> safeSelectQuery;
    private T newValues;

    SafeUpdateBuilder(T newValues, ISafeSelectQuery<T> safeSelectQuery) {
        this.newValues = newValues;
        this.safeSelectQuery = safeSelectQuery;
    }

    @Override
    public Statement build() {
        String tableName = QueryUtil.getTableName(newValues.getClass());
        Update update = QueryBuilder.update(tableName);
        QueryUtil.setValues(newValues, (c, v) -> update.with(QueryBuilder.set(c, v)),
                            f -> f.getDeclaredAnnotation(PartitionKey.class) == null && f
                                    .getDeclaredAnnotation(ClusteringColumn.class) == null);
        Update.Where where = update.where();
        safeSelectQuery.setWhere(where::and);
        LOGGER.debug("Update: {}", where.toString());
        return where;
    }

}
