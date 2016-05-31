package com.bazarnazar.cassandramapings.context;

import com.bazarnazar.cassandramapings.querybuilder.impl.SafeSelectQuery;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;

/**
 * Created by Bazar on 25.05.16.
 */
public interface ICassandraManager {

    <T> Result<T> query(T primaryKey, Class<T> clazz);

    <T> Result<T> query(T primaryKey, Class<T> clazz, PagingState pagingState);

    <T> Result<T> query(SafeSelectQuery<T> safeSelectQuery, Class<T> clazz);

    <T> T query(Statement statement, Class<T> clazz);

    <T, D> Result<T> queryByDependentTable(D primaryKey, Class<D> dependentClass,
            Class<T> entityClass);

    <T, D> Result<T> queryByDependentTable(D primaryKey, Class<D> dependentClass,
            Class<T> entityClass, PagingState pagingState);

    <T, D> Result<T> queryByDependentTable(SafeSelectQuery<D> safeSelectQuery,
            Class<D> dependentClass, Class<T> entityClass);

    <T, D> Result<T> queryByDependentTable(Statement statement, Class<D> dependentClass,
            Class<T> entityClass);

    <T> void save(T entity);

    <T> void remove(T entity);

    <T> void saveInBatch(T entity);

    <T> void removeInBatch(T entity);

    void openBatch();

    void executeBatch();

    Session getSession();

}
