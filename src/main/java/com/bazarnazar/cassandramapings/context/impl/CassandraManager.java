package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.datastax.driver.core.Session;

/**
 * Created by Bazar on 25.05.16.
 */
public class CassandraManager implements ICassandraManager {


    @Override
    public <T, ID> T getById(ID id, Class<T> clazz) {
        return null;
    }

    @Override
    public <T, Q> T query(Q query, Class<T> clazz) {
        return null;
    }

    @Override
    public <T> void save(T entity) {

    }

    @Override
    public <T> void remove(T entity) {

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
