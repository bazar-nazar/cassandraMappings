package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.IContextConfiguration;
import com.bazarnazar.cassandramapings.context.IDataImporter;
import com.bazarnazar.cassandramapings.util.DataImportUtil;
import com.bazarnazar.cassandramapings.util.Tuple;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by Bazar on 27.05.16.
 */
public class DataImporter implements IDataImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataImporter.class);

    @Override
    public void importData() {
        IContextConfiguration configuration = CassandraContext.getInstance().getConfiguration();
        MappingManager mappingManager = CassandraContext.getInstance().getMappingManager();
        Set<Class<?>> entityClasses = CassandraContext.getInstance().getEntitiesClasses();
        switch (configuration.getImportPolicy()) {
            case ADD:
                entityClasses.stream().map(e -> e.getDeclaredAnnotation(Table.class).name())
                             .filter(DataImportUtil::isFileExists)
                             .flatMap(DataImportUtil::getInserts)
                             .peek(q -> LOGGER.debug("Inserting data: {}", q))
                             .forEach(mappingManager.getSession()::execute);
                break;
            case REPLACE:
                entityClasses.stream().map(e -> e.getDeclaredAnnotation(Table.class).name())
                             .filter(DataImportUtil::isFileExists)
                             .map(name -> new Tuple<>(name, DataImportUtil.getTruncate(name)))
                             .peek(t -> LOGGER.info("Clear table {}: {}", t._1, t._2))
                             .peek(t -> mappingManager.getSession().execute(t._2))
                             .flatMap(t -> DataImportUtil.getInserts(t._1))
                             .peek(q -> LOGGER.debug("Inserting data: {}", q))
                             .forEach(mappingManager.getSession()::execute);
                break;
            case IFEMPTY:
                //todo
                break;
        }
    }
}
