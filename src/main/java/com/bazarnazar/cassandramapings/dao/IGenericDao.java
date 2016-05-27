package com.bazarnazar.cassandramapings.dao;

/**
 * Created by Bazar on 25.05.16.
 */
public interface IGenericDao<T, ID> {

    T getById(ID id);

    //todo design of criteria
    T getByCriteria(Object criteria);

    void save(T entity);

    void delete(T entity);

}
