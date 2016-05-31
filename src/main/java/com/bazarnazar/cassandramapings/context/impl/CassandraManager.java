package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.bazarnazar.cassandramapings.querybuilder.impl.SafeSelectQuery;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;

/**
 * Created by Bazar on 25.05.16.
 */
public class CassandraManager implements ICassandraManager {

    @Override
    public <T> Result<T> query(T primaryKey, Class<T> clazz) {
        return null;
    }

    @Override
    public <T> Result<T> query(T primaryKey, Class<T> clazz, PagingState pagingState) {
        return null;
    }

    @Override
    public <T> Result<T> query(SafeSelectQuery<T> safeSelectQuery, Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T query(Statement statement, Class<T> clazz) {
        return null;
    }

    @Override
    public <T, D> Result<T> queryByDependentTable(D primaryKey, Class<D> dependentClass,
            Class<T> entityClass) {
        return null;
    }

    @Override
    public <T, D> Result<T> queryByDependentTable(D primaryKey, Class<D> dependentClass,
            Class<T> entityClass, PagingState pagingState) {
        return null;
    }

    @Override
    public <T, D> Result<T> queryByDependentTable(SafeSelectQuery<D> safeSelectQuery,
            Class<D> dependentClass, Class<T> entityClass) {
        return null;
    }

    @Override
    public <T, D> Result<T> queryByDependentTable(Statement statement, Class<D> dependentClass,
            Class<T> entityClass) {
        return null;
    }

    @Override
    public <T> void save(T entity) {

    }

    @Override
    public <T> void remove(T entity) {

    }

    @Override
    public <T> void saveInBatch(T entity) {

    }

    @Override
    public <T> void removeInBatch(T entity) {

    }

    @Override
    public void openBatch() {

    }

    @Override
    public void executeBatch() {

    }

    @Override
    public Session getSession() {
        return null;
    }
}
