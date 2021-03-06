package com.bazarnazar.cassandramapings.querybuilder;

import com.bazarnazar.cassandramapings.exceptions.QueryBuilderException;
import com.bazarnazar.cassandramapings.util.EntityDefinitionUtil;
import com.bazarnazar.cassandramapings.util.Tuple;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Bazar on 30.05.16.
 */
public class SafeSelectQuery<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SafeSelectQuery.class);

    private String tableName;
    private SafeSelectQuery<T> instance;
    private T queryObject;
    private ColumnAccessHandler columnAccessHandler;
    private final Set<Condition> conditions = new HashSet<>();

    SafeSelectQuery(Class<T> entityClass) {
        try {
            Table table;
            if ((table = entityClass.getDeclaredAnnotation(Table.class)) == null) {
                throw new QueryBuilderException("Cant create query builder: " + entityClass
                        .getName() + " doesn't have @Table annotation");
            }
            tableName = table.name();

            Tuple<T, ColumnAccessHandler> tuple = ColumnAccessHandler.proxyEntity(entityClass);
            queryObject = tuple._1;
            columnAccessHandler = tuple._2;
            instance = this;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                InvocationTargetException e) {
            throw new QueryBuilderException("Cant create query builder ", e);
        }
    }


    public <R> Condition<R> where(Function<T, R> extractor) {
        return new Condition<>(extractor);
    }

    public <R> Condition<R> and(Function<T, R> extractor) {
        return where(extractor);
    }

    public Statement build() {
        if (conditions.stream()
                      .anyMatch(c -> c.partitionKey == null && c.clusteringColumn == null)) {
            throw new QueryBuilderException("Invalid query");
        }
        List<Condition> orderedConditions = conditions.stream().sorted()
                                                      .collect(Collectors.toList());
        long partitionKeySize = orderedConditions.stream().filter(c -> c.partitionKey != null)
                                                 .count();
        if (IntStream.range(0, orderedConditions.size()).anyMatch(
                i -> i < partitionKeySize ? orderedConditions.get(i).partitionKey
                        .value() != i : orderedConditions.get(i).clusteringColumn
                        .value() != i - partitionKeySize)) {
            throw new QueryBuilderException("Invalid query");
        }
        Select.Where where = QueryBuilder.select().all().from(tableName).where();
        orderedConditions.stream().map(Condition::getClause).forEach(where::and);
        LOGGER.debug("Created select statement: {}", where.toString());
        return where;
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

        private SafeSelectQuery<T> condition(ConditionType conditionType, R value) {
            this.value = value;
            this.conditionType = conditionType;
            extractor.apply(queryObject);
            Field columnField = columnAccessHandler.getLastAccessedColumnField();
            columnName = EntityDefinitionUtil.getColumnName(columnField);
            partitionKey = columnField.getDeclaredAnnotation(PartitionKey.class);
            clusteringColumn = columnField.getDeclaredAnnotation(ClusteringColumn.class);
            conditions.add(this);
            return instance;
        }

        public SafeSelectQuery<T> eq(R value) {
            return condition(ConditionType.EQ, value);
        }

        public SafeSelectQuery<T> in(Set<R> values) {
            this.values = new HashSet<>();
            this.values.addAll(values);
            return condition(ConditionType.IN, null);
        }

        public SafeSelectQuery<T> lt(R value) {
            return condition(ConditionType.LT, value);
        }

        public SafeSelectQuery<T> lte(R value) {
            return condition(ConditionType.LTE, value);
        }

        public SafeSelectQuery<T> gt(R value) {
            return condition(ConditionType.GT, value);
        }

        public SafeSelectQuery<T> gte(R value) {
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
