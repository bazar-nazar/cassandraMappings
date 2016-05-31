package com.bazarnazar.cassandramapings.querybuilder;

import com.datastax.driver.core.Statement;

import java.util.Set;

/**
 * Created by Bazar on 31.05.16.
 */
public interface ISafeSelectQuery<T> {

    Statement build();

}
