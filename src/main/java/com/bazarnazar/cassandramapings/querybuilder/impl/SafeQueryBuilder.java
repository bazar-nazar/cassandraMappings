package com.bazarnazar.cassandramapings.querybuilder.impl;


import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQueryInitial;

/**
 * Created by Bazar on 30.05.16.
 */
public final class SafeQueryBuilder {

    private SafeQueryBuilder() {
    }

    public static <T> ISafeSelectQueryInitial<T> select(Class<T> entityClass) {
        return new SafeSelectQuery<>(entityClass);
    }

    public static <T> ISafeSelectQueryInitial<T> select(T entity) {
        ISafeSelectQueryInitial<T> safeSelectQueryInitial = new SafeSelectQuery<>(
                (Class<T>) entity.getClass());
        safeSelectQueryInitial.setQueryObject(entity);
        return safeSelectQueryInitial;
    }

    public static <T> InsertQuery<T> insert(T entity) {
        return new InsertQuery<>(entity);
    }

}
