package com.bazarnazar.cassandramapings.context;

import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQuery;
import com.bazarnazar.cassandramapings.context.impl.ComplexResult;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;

/**
 * Created by Bazar on 25.05.16.
 */
public interface ICassandraManager {

    <T> Result<T> all(Class<T> clazz);

    <T> Result<T> query(T primaryKey);

    <T> Result<T> query(T primaryKey, PagingState pagingState);

    <T> Result<T> query(ISafeSelectQuery<T> safeSelectQuery);

    <T> Result<T> query(Statement statement, Class<T> clazz);

    <T, D> ComplexResult<T, D> queryByDependentTable(D primaryKey, Class<T> entityClass);

    <T, D> ComplexResult<T, D> queryByDependentTable(D primaryKey, Class<T> entityClass,
            PagingState pagingState);

    <T, D> ComplexResult<T, D> queryByDependentTable(ISafeSelectQuery<D> safeSelectQuery,
            Class<T> entityClass);

    <T, D> ComplexResult<T, D> queryByDependentTable(Statement statement, Class<D> dependentClass,
            Class<T> entityClass);

    <T, D> ComplexResult<T, D> queryDistinctByDependentTable(D primaryKey, Class<T> entityClass);

    <T, D> ComplexResult<T, D> queryDistinctByDependentTable(D primaryKey, Class<T> entityClass,
            PagingState pagingState);

    <T, D> ComplexResult<T, D> queryDistinctByDependentTable(ISafeSelectQuery<D> safeSelectQuery,
            Class<T> entityClass);

    <T, D> ComplexResult<T, D> queryDistinctByDependentTable(Statement statement, Class<D> dependentClass,
            Class<T> entityClass);

    <T> T refresh(T entity);

    <T> void save(T entity);

    <T> void remove(T entity);

    <T> void saveInBatch(T entity);

    <T> void removeInBatch(T entity);

    void openBatch();

    void executeBatch();

    Session getSession();

}
