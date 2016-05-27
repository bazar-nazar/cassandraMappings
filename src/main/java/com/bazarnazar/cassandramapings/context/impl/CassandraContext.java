package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.bazarnazar.cassandramapings.context.IContextConfiguration;
import com.bazarnazar.cassandramapings.exceptions.CassandraInitializationException;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by Bazar on 25.05.16.
 */
public final class CassandraContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraContext.class);

    private static CassandraContext ourInstance = new CassandraContext();

    public static CassandraContext getInstance() {
        return ourInstance;
    }

    private CassandraContext() {
    }

    private IContextConfiguration configuration;
    private Cluster cluster;
    private Session session;
    private MappingManager mappingManager;
    private Set<Class<?>> entitiesClasses;
    private ICassandraManager cassandraManager;

    public IContextConfiguration getConfiguration() {
        return configuration;
    }

    public MappingManager getMappingManager() {
        return mappingManager;
    }

    public Set<Class<?>> getEntitiesClasses() {
        return entitiesClasses;
    }

    public void setEntitiesClasses(Set<Class<?>> entitiesClasses) {
        this.entitiesClasses = entitiesClasses;
    }

    public ICassandraManager getCassandraManager() {
        return cassandraManager;
    }

    public void init(IContextConfiguration configuration) {
        try {
            LOGGER.info("Starting");
            this.configuration = configuration;
            configuration.confugure();
            initCluster(configuration);
            session = cluster.connect(configuration.getKeyspace());
            mappingManager = new MappingManager(session);
            ModelBuilder validator = new ModelBuilder();
            validator.parseDataModel(mappingManager, configuration);
            DataImporter dataImporter = new DataImporter();
            dataImporter.importData();
        } catch (CassandraInitializationException e) {
            LOGGER.error("Initialization Error", e);
        }
    }

    public void stop() {
        cluster.close();
    }

    private void initCluster(IContextConfiguration configuration) {
        LOGGER.info("Connecting to cluster");
        Cluster.Builder builder = Cluster.builder();
        configuration.getContactPoints().forEach(builder::addContactPoint);
        cluster = builder.build();
    }

}
