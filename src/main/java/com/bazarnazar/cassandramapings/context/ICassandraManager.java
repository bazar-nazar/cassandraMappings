package com.bazarnazar.cassandramapings.context;

/**
 * Created by Bazar on 25.05.16.
 */
public interface ICassandraManager {

    <T, ID> T getById(ID id);

    <T, Q> T query(Q query);

    <T> void save(T entity);

    <T> void remove(T entity);

}
