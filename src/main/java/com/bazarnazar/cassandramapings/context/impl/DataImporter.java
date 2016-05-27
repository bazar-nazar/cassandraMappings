package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.IContextConfiguration;
import com.bazarnazar.cassandramapings.context.IDataImporter;
import com.datastax.driver.mapping.MappingManager;

import java.util.Set;

/**
 * Created by Bazar on 27.05.16.
 */
public class DataImporter implements IDataImporter {

    private IContextConfiguration configuration;
    private MappingManager mappingManager;
    private Set<Class<?>> entityClasses;

    @Override
    public void importData() {
        configuration = CassandraContext.getInstance().getConfiguration();
        mappingManager = CassandraContext.getInstance().getMappingManager();
        entityClasses = CassandraContext.getInstance().getEntitiesClasses();
        //todo implement
    }

}
