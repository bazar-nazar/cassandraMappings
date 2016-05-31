package com.bazarnazar.cassandramapings.querybuilder;

import com.bazarnazar.cassandramapings.querybuilder.impl.SafeSelectQuery;

import java.util.function.Function;

/**
 * Created by Bazar on 31.05.16.
 */
public interface ISafeSelectQueryNext<T> extends ISafeSelectQuery<T> {

    <R> SafeSelectQuery<T>.Condition<R> and(Function<T, R> extractor);
}