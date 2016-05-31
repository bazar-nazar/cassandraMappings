package com.bazarnazar.cassandramapings.querybuilder;


/**
 * Created by Bazar on 30.05.16.
 */
public final class SafeQueryBuilder {

    private SafeQueryBuilder() {
    }

    public static <T> SafeSelectQuery<T> select(Class<T> entityClass) {
        return new SafeSelectQuery<>(entityClass);
    }

}
