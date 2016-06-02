package com.bazarnazar.cassandramapings.querybuilder;

import com.datastax.driver.core.Statement;

/**
 * Created by Bazar on 02.06.16.
 */
public interface IQueryBuilder<T> {
    Statement build();
}
