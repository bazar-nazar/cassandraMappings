package com.bazarnazar.cassandramapings.context;

import com.datastax.driver.mapping.MappingManager;

import java.util.Map;
import java.util.Set;

/**
 * Created by Bazar on 26.05.16.
 */
public interface IModelBuilder {

    Map<Class<?>, Map<String, Set<Class<?>>>> parseDataModel(MappingManager mappingManager, IContextConfiguration contextConfiguration);
}
