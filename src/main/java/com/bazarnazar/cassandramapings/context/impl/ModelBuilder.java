package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.annotations.DependentTables;
import com.bazarnazar.cassandramapings.context.IContextConfiguration;
import com.bazarnazar.cassandramapings.context.IModelBuilder;
import com.bazarnazar.cassandramapings.context.ValidationPolicy;
import com.bazarnazar.cassandramapings.exceptions.ValidationException;
import com.bazarnazar.cassandramapings.util.EntityDefinitionUtil;
import com.bazarnazar.cassandramapings.util.Tuple;
import com.datastax.driver.core.ClusteringOrder;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Bazar on 26.05.16.
 */
public class ModelBuilder implements IModelBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelBuilder.class);

    private MappingManager mappingManager;
    private IContextConfiguration contextConfiguration;
    private Map<Class<?>, Map<String, Set<Class<?>>>> dataModelGraph = new HashMap<>();

    @Override
    public Map<Class<?>, Map<String, Set<Class<?>>>> parseDataModel(MappingManager mappingManager,
            IContextConfiguration contextConfiguration) {
        this.mappingManager = mappingManager;
        this.contextConfiguration = contextConfiguration;
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()));
        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Table.class);
        entities.forEach(this::addEntityToGraph);
        CassandraContext.getInstance().setEntitiesClasses(entities);
        if (contextConfiguration.getValidationPolicy() != ValidationPolicy.NONE) {
            LOGGER.info("Validating data model");
            this.mappingManager = mappingManager;
            this.contextConfiguration = contextConfiguration;
            entities.forEach(this::validateEntity);
        }
        return dataModelGraph;
    }

    private void addEntityToGraph(Class<?> clazz) {
        Map<String, Set<Class<?>>> tableDependencies = new HashMap<>();
        if (clazz.getDeclaredAnnotation(DependentTables.class) != null) {
            Set<String> columns = EntityDefinitionUtil.defineColumns(clazz).keySet();
            Set<Class<?>> dependentClasses = Arrays
                    .stream(clazz.getDeclaredAnnotation(DependentTables.class).value())
                    .collect(Collectors.toSet());
            validateDependencies(clazz, columns, dependentClasses);
            addTableDependencies(tableDependencies, columns, dependentClasses);
        }
        dataModelGraph.put(clazz, tableDependencies);
    }

    private void addTableDependencies(Map<String, Set<Class<?>>> tabelDependencies,
            Set<String> columns, Set<Class<?>> dependentClasses) {
        Set<Tuple<Class<?>, Set<String>>> dependentClassesSet = dependentClasses.stream()
                                                                                .map(c -> new
                                                                                        Tuple<Class<?>, Set<String>>(
                                                                                        c,
                                                                                        EntityDefinitionUtil
                                                                                                .defineColumns(
                                                                                                        c)
                                                                                                .keySet()))
                                                                                .collect(
                                                                                        HashSet::new,
                                                                                        HashSet::add,
                                                                                        AbstractCollection::addAll);
        columns.stream().map(name -> new Tuple<String, Set<Class<?>>>(name,
                                                                      dependentClassesSet.stream()
                                                                                         .filter(t -> t._2
                                                                                                 .contains(
                                                                                                         name))
                                                                                         .map(t -> t._1)
                                                                                         .collect(
                                                                                                 Collectors
                                                                                                         .toSet())))
               .forEach(t -> tabelDependencies.put(t._1, t._2));
    }

    private void validateDependencies(Class<?> clazz, Set<String> columns,
            Set<Class<?>> dependentClasses) {
        if (!columns.containsAll(dependentClasses.stream().flatMap(
                c -> EntityDefinitionUtil.getOrderedItems(c, PartitionKey.class).stream()
                                         .map(t -> t._1)).collect(Collectors.toSet()))) {
            throw new ValidationException("Scheme definition exception. One of " + clazz
                    .getName() + " dependencies can not be queried from it");
        }
    }

    private void validateEntity(Class<?> clazz) {
        Mapper mapper = mappingManager.mapper(clazz);
        TableMetadata tableMetadata = mapper.getTableMetadata();
        if (ValidationPolicy.DROPCREATE.equals(contextConfiguration.getValidationPolicy())) {
            if (tableMetadata != null) {
                String query = EntityDefinitionUtil.dropTableQuery(tableMetadata.getName());
                LOGGER.debug("Dropping table: {}", query);
                mappingManager.getSession().execute(query);
            }
            createTable(clazz);
        }
    }

    private void createTable(Class<?> clazz) {
        String name = clazz.getDeclaredAnnotation(Table.class).name();
        Map<String, String> columns = EntityDefinitionUtil.defineColumns(clazz);
        List<Tuple<String, ClusteringOrder>> partitionKey = EntityDefinitionUtil
                .getOrderedItems(clazz, PartitionKey.class);
        List<Tuple<String, ClusteringOrder>> clusteringColumns = EntityDefinitionUtil
                .getOrderedItems(clazz, ClusteringColumn.class);
        EntityDefinitionUtil.checkStaticColumns(clazz, columns);
        String query = EntityDefinitionUtil
                .createTableQuery(name, columns, partitionKey, clusteringColumns);
        LOGGER.debug("Creating table: {}", query);
        mappingManager.getSession().execute(query);
    }

}
