package com.bazarnazar.cassandramapings.querybuilder;


import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQueryInitial;
import com.bazarnazar.cassandramapings.querybuilder.impl.SafeSelectQuery;

/**
 * Created by Bazar on 30.05.16.
 */
public final class SafeQueryBuilder {

    private SafeQueryBuilder() {
    }

    public static <T> ISafeSelectQueryInitial<T> select(Class<T> entityClass) {
        return new SafeSelectQuery<>(entityClass);
    }

}
