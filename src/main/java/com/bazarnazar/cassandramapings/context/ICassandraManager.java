package com.bazarnazar.cassandramapings.context;

import com.datastax.driver.core.Session;

/**
 * Created by Bazar on 25.05.16.
 */
public interface ICassandraManager {

    <T, ID> T getById(ID id, Class<T> clazz);

    <T, Q> T query(Q query, Class<T> clazz);

    <T> void save(T entity);

    <T> void remove(T entity);

    void openBatch();

    void executeBatch();

    Session getSession();

}
