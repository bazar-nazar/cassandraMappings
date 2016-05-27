package com.bazarnazar.cassandramapings.context;

import java.util.Set;

/**
 * Created by Bazar on 25.05.16.
 */
public interface IContextConfiguration {

    void confugure();

    String getKeyspace();

    void setKeyspace(String keyspace);

    void addContactPoint(String connectionPoint);

    void removeContactPint(String connectionPoint);

    Set<String> getContactPoints();

    ValidationPolicy getValidationPolicy();

    void setValidationPolicy(ValidationPolicy validationPolicy);
}
