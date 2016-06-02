package com.bazarnazar.cassandramapings.annotations;

import com.datastax.driver.core.querybuilder.QueryBuilder;

/**
 * Created by Bazar on 02.06.16.
 */
public enum GeneratorType {

    UUID, NOW;

    public Object getOperation() {
        switch (this) {
            case UUID:
                return QueryBuilder.uuid();
            case NOW:
                return QueryBuilder.now();
            default:
                return null;
        }
    }

}
