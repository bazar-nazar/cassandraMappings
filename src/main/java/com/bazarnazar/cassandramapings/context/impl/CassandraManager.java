package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.datastax.driver.core.Session;

/**
 * Created by Bazar on 25.05.16.
 */
public class CassandraManager implements ICassandraManager {

    private Session session;

    public CassandraManager(Session session) {
        this.session = session;
    }

    void stop() {
        session.close();
    }


    @Override
    public <T, ID> T getById(ID id) {
        return null;
    }

    @Override
    public <T, Q> T query(Q query) {
        return null;
    }

    @Override
    public <T> void save(T entity) {

    }

    @Override
    public <T> void remove(T entity) {

    }

}
