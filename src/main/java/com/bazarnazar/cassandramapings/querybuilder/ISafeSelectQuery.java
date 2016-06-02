package com.bazarnazar.cassandramapings.querybuilder;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.querybuilder.Clause;

import java.util.function.Consumer;

/**
 * Created by Bazar on 31.05.16.
 */
public interface ISafeSelectQuery<T> extends IQueryBuilder<T> {

    void setQueryObject(T queryObject);

    ISafeSelectQuery<T> setPagingState(PagingState pagingState);

    void setWhere(Consumer<Clause> clauseConsumer);

    Class<T> getEntityClass();

}
