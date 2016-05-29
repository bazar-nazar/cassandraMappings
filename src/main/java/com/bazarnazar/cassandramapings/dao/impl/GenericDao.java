package com.bazarnazar.cassandramapings.dao.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.bazarnazar.cassandramapings.context.impl.CassandraContext;
import com.bazarnazar.cassandramapings.dao.IGenericDao;

/**
 * Created by Bazar on 25.05.16.
 */
public class GenericDao<T, ID> implements IGenericDao<T, ID> {

    private ICassandraManager cassandraManager = CassandraContext.getInstance()
                                                                 .createCassandraManager();

    protected ICassandraManager getCassandraManager() {
        return cassandraManager;
    }

    public GenericDao() {
    }

    @Override
    public T getById(ID id) {
        return null;
    }

    @Override
    public T getByCriteria(Object criteria) {
        return null;
    }

    @Override
    public void save(T entity) {

    }

    @Override
    public void delete(T entity) {

    }
}
