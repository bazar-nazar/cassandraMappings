package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.IContextConfiguration;
import com.bazarnazar.cassandramapings.context.ImportPolicy;
import com.bazarnazar.cassandramapings.context.ValidationPolicy;
import com.datastax.driver.core.Session;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Bazar on 25.05.16.
 */
public abstract class AContextConfiguration implements IContextConfiguration {

    private Session session;
    private ValidationPolicy validationPolicy = ValidationPolicy.VALIDATE;
    private ImportPolicy importPolicy = ImportPolicy.REPLACE;

    @Override
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public ValidationPolicy getValidationPolicy() {
        return validationPolicy;
    }

    public void setValidationPolicy(ValidationPolicy validationPolicy) {
        this.validationPolicy = validationPolicy;
    }

    @Override
    public ImportPolicy getImportPolicy() {
        return importPolicy;
    }

    public void setImportPolicy(ImportPolicy importPolicy) {
        this.importPolicy = importPolicy;
    }
}
