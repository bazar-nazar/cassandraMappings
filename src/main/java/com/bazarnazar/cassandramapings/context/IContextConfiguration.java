package com.bazarnazar.cassandramapings.context;

import com.datastax.driver.core.Session;

import java.util.Set;

/**
 * Created by Bazar on 25.05.16.
 */
public interface IContextConfiguration {

    void confugure();

    Session getSession();

    ValidationPolicy getValidationPolicy();

    ImportPolicy getImportPolicy();
}
