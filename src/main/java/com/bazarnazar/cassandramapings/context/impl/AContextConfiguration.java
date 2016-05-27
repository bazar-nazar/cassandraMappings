package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.IContextConfiguration;
import com.bazarnazar.cassandramapings.context.ImportPolicy;
import com.bazarnazar.cassandramapings.context.ValidationPolicy;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Bazar on 25.05.16.
 */
public abstract class AContextConfiguration implements IContextConfiguration {

    private String keyspace = "";
    private final Set<String> contactPoints = new HashSet<>();
    private ValidationPolicy validationPolicy = ValidationPolicy.VALIDATE;
    private ImportPolicy importPolicy = ImportPolicy.REPLACE;

    @Override
    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public void addContactPoint(String contactPoint) {
        contactPoints.add(contactPoint);
    }

    public void removeContactPint(String contactPoint) {
        contactPoints.remove(contactPoint);
    }

    @Override
    public Set<String> getContactPoints() {
        return contactPoints;
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
