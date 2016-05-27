package com.bazarnazar.cassandramapings.context;

import java.util.Set;

/**
 * Created by Bazar on 25.05.16.
 */
public interface IContextConfiguration {

    void confugure();

    String getKeyspace();

    Set<String> getContactPoints();

    ValidationPolicy getValidationPolicy();

    ImportPolicy getImportPolicy();
}
