package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.annotations.IndexingTable;
import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.bazarnazar.cassandramapings.exceptions.QueryException;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQuery;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQueryInitial;
import com.bazarnazar.cassandramapings.querybuilder.impl.SafeQueryBuilder;
import com.bazarnazar.cassandramapings.util.QueryUtil;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by Bazar on 25.05.16.
 */
public class CassandraManager implements ICassandraManager {

    private static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    private MappingManager mappingManager;
    private Map<Class<?>, Map<String, Set<Dependency>>> dataModelGraph;

    public CassandraManager(MappingManager mappingManager,
            Map<Class<?>, Map<String, Set<Dependency>>> dataModelGraph) {
        this.mappingManager = mappingManager;
        this.dataModelGraph = dataModelGraph;
    }

    @Override
    public <T> ProxyResult<T> all(Class<T> clazz) {
        return query(SafeQueryBuilder.select(clazz));
    }

    @Override
    public <T> ProxyResult<T> query(T primaryKey) {
        return query(primaryKey, null);
    }

    @Override
    public <T> ProxyResult<T> query(T primaryKey, PagingState pagingState) {
        ISafeSelectQueryInitial<T> safeSelectQueryInitial = SafeQueryBuilder
                .createPkQuery(primaryKey);
        safeSelectQueryInitial.setPagingState(pagingState);
        return query(safeSelectQueryInitial);
    }

    @Override
    public <T> ProxyResult<T> query(ISafeSelectQuery<T> safeSelectQuery) {
        return query(safeSelectQuery.build(), safeSelectQuery.getEntityClass());
    }

    @Override
    public <T> ProxyResult<T> query(Statement statement, Class<T> clazz) {
        Mapper<T> mapper = mappingManager.mapper(clazz);
        return new ProxyResult<>(mapper.map(mappingManager.getSession().execute(statement)));
    }

    @Override
    public <T, D> ComplexResult<T, D> queryByDependentTable(D primaryKey, Class<T> entityClass) {
        return new ComplexResult<>(query(primaryKey), mapperFactory
                .getMapperFacade((Class<D>) primaryKey.getClass(), entityClass), this);
    }

    @Override
    public <T, D> ComplexResult<T, D> queryByDependentTable(D primaryKey, Class<T> entityClass,
            PagingState pagingState) {
        return new ComplexResult<>(query(primaryKey, pagingState), mapperFactory
                .getMapperFacade((Class<D>) primaryKey.getClass(), entityClass), this);
    }

    @Override
    public <T, D> ComplexResult<T, D> queryByDependentTable(ISafeSelectQuery<D> safeSelectQuery,
            Class<T> entityClass) {
        return new ComplexResult<>(query(safeSelectQuery), mapperFactory
                .getMapperFacade(safeSelectQuery.getEntityClass(), entityClass), this);
    }

    @Override
    public <T, D> ComplexResult<T, D> queryByDependentTable(Statement statement,
            Class<D> dependentClass, Class<T> entityClass) {
        return new ComplexResult<>(query(statement, dependentClass),
                                   mapperFactory.getMapperFacade(dependentClass, entityClass),
                                   this);
    }

    @Override
    public <T, D> ComplexResult<T, D> queryDistinctByDependentTable(D primaryKey,
            Class<T> entityClass) {
        return new DistinctResult<>(query(primaryKey), mapperFactory
                .getMapperFacade((Class<D>) primaryKey.getClass(), entityClass), this);
    }

    @Override
    public <T, D> ComplexResult<T, D> queryDistinctByDependentTable(D primaryKey,
            Class<T> entityClass, PagingState pagingState) {
        return new DistinctResult<>(query(primaryKey, pagingState), mapperFactory
                .getMapperFacade((Class<D>) primaryKey.getClass(), entityClass), this);
    }

    @Override
    public <T, D> ComplexResult<T, D> queryDistinctByDependentTable(
            ISafeSelectQuery<D> safeSelectQuery, Class<T> entityClass) {
        return new DistinctResult<>(query(safeSelectQuery), mapperFactory
                .getMapperFacade(safeSelectQuery.getEntityClass(), entityClass), this);
    }

    @Override
    public <T, D> ComplexResult<T, D> queryDistinctByDependentTable(Statement statement,
            Class<D> dependentClass, Class<T> entityClass) {
        return new DistinctResult<>(query(statement, dependentClass),
                                    mapperFactory.getMapperFacade(dependentClass, entityClass),
                                    this);
    }

    @Override
    public <T> T refresh(T entity) {
        return query(entity).one();
    }

    @Override
    public <T> void save(T entity) {
        if (QueryUtil.getEntityClass(entity).getDeclaredAnnotation(IndexingTable.class) != null) {
            throw new QueryException(QueryUtil.getEntityClass(entity)
                                              .getName() + " is indexing table and should not be " +
                                             "updated manually");
        }
        BatchStatement batchStatement = new BatchStatement();
        saveStatements(entity).forEach(batchStatement::add);
        mappingManager.getSession().execute(batchStatement);
    }

    private <T> Stream<Statement> saveStatements(T entity) {

        //        if (entity instanceof IPojo) {
        //            IPojo pojo = (IPojo) entity;
        //            Stream<Statement> statementStream = pojo.getModifiedColumns().flatMap(
        //                    columnName -> dataModelGraph.get(entity.getClass().getSuperclass())
        //                                                .get(columnName).stream())
        //                                                    .filter(new Dependency
        //                                                            .CompactDependenciesFilter())
        //                                                    .flatMap(
        //                                                            d -> dependencyToStatements
        // (entity, d));
        //            return Stream.concat(statementStream, Stream.of(
        //                    mappingManager.mapper(pojo.getStoredObject().getClass())
        // .saveQuery(entity)));
        //        } else {
        //            mappingManager.getSession().execute(SafeQueryBuilder.insert(entity).build());
        //        }
        return Stream.of(SafeQueryBuilder.update(entity, SafeQueryBuilder.createPkQuery(entity))
                                         .build());
    }


    private <T> Stream<Statement> dependencyToStatements(T entity, Dependency dependency) {
        try {
            Object dependentEntity = dependency.getDependentClass().newInstance();
            if (dependency.isPkDependency()) {
                return Stream.empty();
            } else {
                mapperFactory.getMapperFacade(entity.getClass(), dependency.getDependentClass())
                             .map(entity, dependentEntity);
                dependentEntity = refresh(dependentEntity);
                mapperFactory.getMapperFacade(entity.getClass(), dependency.getDependentClass())
                             .map(entity, dependentEntity);
                return saveStatements(dependentEntity);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public <T> void delete(T entity) {
        SafeQueryBuilder.delete(SafeQueryBuilder.createPkQuery(entity));
    }

    private <T> Stream<Statement> removeStatements(T entity) {
        //        dataModelGraph.get(entity.)
        return null;
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
        return mappingManager.getSession();
    }

    @Override
    public MappingManager getMappingManager() {
        return mappingManager;
    }

}
