package com.bazarnazar.cassandramapings.querybuilder.impl;

import com.bazarnazar.cassandramapings.exceptions.QueryBuilderException;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQuery;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQueryInitial;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQueryNext;
import com.bazarnazar.cassandramapings.util.EntityDefinitionUtil;
import com.bazarnazar.cassandramapings.util.QueryUtil;
import com.bazarnazar.cassandramapings.util.Tuple;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Bazar on 30.05.16.
 */
public class SafeSelectBuilder<T> implements ISafeSelectQueryInitial<T>, ISafeSelectQueryNext<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SafeSelectBuilder.class);

    private String tableName;
    private SafeSelectBuilder<T> instance;
    private Class<T> entityClass;
    private T proxyObject;
    private T queryObject;
    private ColumnAccessHandler columnAccessHandler;
    private final Set<Condition> conditions = new HashSet<>();
    private PagingState pagingState;

    SafeSelectBuilder(Class<T> entityClass) {
        try {
            this.entityClass = entityClass;
            tableName = QueryUtil.getTableName(entityClass);
            Tuple<T, ColumnAccessHandler> tuple = ColumnAccessHandler.proxyEntity(entityClass);
            proxyObject = tuple._1;
            columnAccessHandler = tuple._2;
            instance = this;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                InvocationTargetException e) {
            throw new QueryBuilderException("Cant create query builder ", e);
        }
    }


    @Override
    public void setQueryObject(T queryObject) {
        this.queryObject = queryObject;
    }

    @Override
    public <R> Condition<R> where(Function<T, R> extractor) {
        return new Condition<>(extractor);
    }

    @Override
    public <R> Condition<R> and(Function<T, R> extractor) {
        return where(extractor);
    }

    @Override
    public ISafeSelectQuery<T> setPagingState(PagingState pagingState) {
        this.pagingState = pagingState;
        return this;
    }

    @Override
    public Statement build() {
        if (conditions.stream()
                      .anyMatch(c -> c.partitionKey == null && c.clusteringColumn == null)) {
            throw new QueryBuilderException("Invalid query");
        }
        Select.Where where = QueryBuilder.select().all().from(tableName).where();
        setWhere(where::and);
        LOGGER.debug("Created select statement: {}", where.toString());
        if (pagingState != null) {
            return where.setPagingState(pagingState);
        }
        return where;
    }

    @Override
    public void setWhere(Consumer<Clause> clauseConsumer) {
        List<Condition> orderedConditions = conditions.stream().sorted()
                                                      .collect(Collectors.toList());
        orderedConditions.stream().map(Condition::getClause).forEach(clauseConsumer);
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    private enum ConditionType {
        EQ, IN, LT, LTE, GT, GTE
    }

    public class Condition<R> implements Comparable<Condition> {
        private Function<T, R> extractor;
        private ConditionType conditionType;
        private String columnName;
        private PartitionKey partitionKey;
        private ClusteringColumn clusteringColumn;
        private R value;
        private Set<R> values;

        public Condition(Function<T, R> extractor) {
            this.extractor = extractor;
        }

        private Clause getClause() {
            switch (conditionType) {
                case EQ:
                    return QueryBuilder.eq(columnName, value);
                case IN:
                    return QueryBuilder
                            .in(columnName, values.stream().collect(Collectors.toList()));
                case LT:
                    return QueryBuilder.lt(columnName, value);
                case LTE:
                    return QueryBuilder.lte(columnName, value);
                case GT:
                    return QueryBuilder.gt(columnName, value);
                case GTE:
                    return QueryBuilder.gte(columnName, value);
                default:
                    return null;
            }
        }

        private SafeSelectBuilder<T> condition(ConditionType conditionType, R value) {
            this.value = value;
            this.conditionType = conditionType;
            extractor.apply(proxyObject);
            Field columnField = columnAccessHandler.getLastAccessedColumnField();
            columnName = EntityDefinitionUtil.getColumnName(columnField);
            partitionKey = columnField.getDeclaredAnnotation(PartitionKey.class);
            clusteringColumn = columnField.getDeclaredAnnotation(ClusteringColumn.class);
            conditions.add(this);
            return instance;
        }

        public ISafeSelectQueryNext<T> eq() {
            return eq(extractor.apply(queryObject));
        }

        public ISafeSelectQueryNext<T> eq(R value) {
            return condition(ConditionType.EQ, value);
        }

        public ISafeSelectQueryNext<T> in(Set<R> values) {
            this.values = new HashSet<>();
            this.values.addAll(values);
            return condition(ConditionType.IN, null);
        }

        public ISafeSelectQueryNext<T> lt() {
            return lt(extractor.apply(queryObject));
        }

        public ISafeSelectQueryNext<T> lt(R value) {
            return condition(ConditionType.LT, value);
        }

        public ISafeSelectQueryNext<T> lte() {
            return lte(extractor.apply(queryObject));
        }

        public ISafeSelectQueryNext<T> lte(R value) {
            return condition(ConditionType.LTE, value);
        }

        public ISafeSelectQueryNext<T> gt() {
            return gt(extractor.apply(queryObject));
        }

        public ISafeSelectQueryNext<T> gt(R value) {
            return condition(ConditionType.GT, value);
        }

        public ISafeSelectQueryNext<T> gte() {
            return gte(extractor.apply(queryObject));
        }

        public ISafeSelectQueryNext<T> gte(R value) {
            return condition(ConditionType.GTE, value);
        }

        @Override
        public int compareTo(Condition o) {
            if (this.clusteringColumn == null && o.clusteringColumn != null) {
                return -1;
            } else if (this.clusteringColumn != null && o.clusteringColumn == null) {
                return 1;
            } else if (this.partitionKey != null && o.partitionKey != null) {
                return this.partitionKey.value() - o.partitionKey.value();
            } else {
                return this.clusteringColumn.value() - o.clusteringColumn.value();
            }
        }

    }
}