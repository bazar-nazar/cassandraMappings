package com.bazarnazar.cassandramapings.context;

import com.datastax.driver.mapping.MappingManager;

/**
 * Created by Bazar on 26.05.16.
 */
public interface IModelBuilder {

    void parseDataModel(MappingManager mappingManager, IContextConfiguration contextConfiguration);
}
