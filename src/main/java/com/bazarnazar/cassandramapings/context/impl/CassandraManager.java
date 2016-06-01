package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.bazarnazar.cassandramapings.exceptions.QueryException;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQuery;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQueryInitial;
import com.bazarnazar.cassandramapings.querybuilder.impl.SafeQueryBuilder;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
    public <T> Result<T> all(Class<T> clazz) {
        return query(SafeQueryBuilder.select(clazz));
    }

    @Override
    public <T> Result<T> query(T primaryKey) {
        return query(primaryKey, null);
    }

    @Override
    public <T> Result<T> query(T primaryKey, PagingState pagingState) {
        ISafeSelectQueryInitial<T> safeSelectQueryInitial = SafeQueryBuilder.select(primaryKey);
        Arrays.stream(primaryKey.getClass().getDeclaredFields())
              .filter(f -> f.getDeclaredAnnotation(PartitionKey.class) != null || f
                      .getDeclaredAnnotation(ClusteringColumn.class) != null)
              .map(f -> CassandraManager
                      .fieldToExtractor(f, f.getType(), safeSelectQueryInitial.getEntityClass()))
              .forEach(extractor -> safeSelectQueryInitial.where(extractor).eq());
        safeSelectQueryInitial.setPagingState(pagingState);
        return query(safeSelectQueryInitial);
    }

    @Override
    public <T> Result<T> query(ISafeSelectQuery<T> safeSelectQuery) {
        return query(safeSelectQuery.build(), safeSelectQuery.getEntityClass());
    }

    @Override
    public <T> Result<T> query(Statement statement, Class<T> clazz) {
        Mapper<T> mapper = mappingManager.mapper(clazz);
        return mapper.map(mappingManager.getSession().execute(statement));
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

    private static <T, R> Function<T, R> fieldToExtractor(Field field, Class<R> fieldClass,
            Class<T> objectClass) {
        try {
            Method accessor = field.getDeclaringClass().getDeclaredMethod(
                    "get" + field.getName().substring(0, 1).toUpperCase() + field.getName()
                                                                                 .substring(1));
            return t -> {
                try {
                    return (R) accessor.invoke(t);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new QueryException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new QueryException(e);
        }
    }
}
