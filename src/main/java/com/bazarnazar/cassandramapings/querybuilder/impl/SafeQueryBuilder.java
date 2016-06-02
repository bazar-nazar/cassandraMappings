package com.bazarnazar.cassandramapings.querybuilder.impl;


import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQuery;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQueryInitial;
import com.bazarnazar.cassandramapings.util.CassandraManagerUtil;
import com.bazarnazar.cassandramapings.util.QueryUtil;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;

import java.util.Arrays;

/**
 * Created by Bazar on 30.05.16.
 */
public final class SafeQueryBuilder {

    private SafeQueryBuilder() {
    }

    public static <T> ISafeSelectQueryInitial<T> select(Class<T> entityClass) {
        return new SafeSelectBuilder<>(entityClass);
    }

    public static <T> ISafeSelectQueryInitial<T> select(T entity) {
        ISafeSelectQueryInitial<T> safeSelectQueryInitial = new SafeSelectBuilder<>(
                QueryUtil.getEntityClass(entity));
        safeSelectQueryInitial.setQueryObject(entity);
        return safeSelectQueryInitial;
    }


    public static <T> ISafeSelectQueryInitial<T> createPkQuery(T primaryKey) {
        ISafeSelectQueryInitial<T> safeSelectQueryInitial = SafeQueryBuilder.select(primaryKey);
        Arrays.stream(QueryUtil.getEntityClass(primaryKey).getDeclaredFields())
              .filter(f -> f.getDeclaredAnnotation(PartitionKey.class) != null || f
                      .getDeclaredAnnotation(ClusteringColumn.class) != null)
              .map(f -> CassandraManagerUtil
                      .fieldToExtractor(f, f.getType(), safeSelectQueryInitial.getEntityClass()))
              .forEach(extractor -> safeSelectQueryInitial.where(extractor).eq());
        return safeSelectQueryInitial;
    }

    public static <T> SafeInsertBuilder<T> insert(T entity) {
        return new SafeInsertBuilder<>(entity);
    }

    public static <T> SafeUpdateBuilder<T> update(T enity, ISafeSelectQuery<T> selectQuery) {
        return new SafeUpdateBuilder<>(enity, selectQuery);
    }

    public static <T> SafeDeleteBuilder<T> delete(Class<T> entityClas,
            ISafeSelectQuery<T> selectQuery) {
        return new SafeDeleteBuilder<>(entityClas, selectQuery);
    }

}
