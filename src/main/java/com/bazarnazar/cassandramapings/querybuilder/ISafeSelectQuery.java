package com.bazarnazar.cassandramapings.querybuilder;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.Statement;

import java.util.Set;

/**
 * Created by Bazar on 31.05.16.
 */
public interface ISafeSelectQuery<T> {

    void setQueryObject(T queryObject);

    ISafeSelectQuery<T> setPagingState(PagingState pagingState);

    Statement build();

    Class<T> getEntityClass();

}
