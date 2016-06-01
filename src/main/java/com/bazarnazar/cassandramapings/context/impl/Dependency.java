package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.util.EntityDefinitionUtil;

/**
 * Created by Bazar on 01.06.16.
 */
public class Dependency {

    public Dependency(Class<?> dependentClass, String columnName) {
        this.dependentClass = dependentClass;
        isPkDependency = EntityDefinitionUtil.getPrimaryKey(dependentClass).map(t -> t._1)
                                             .anyMatch(c -> c.equals(columnName));
    }

    private Class<?> dependentClass;
    private boolean isPkDependency;

    public Class<?> getDependentClass() {
        return dependentClass;
    }

    public void setDependentClass(Class<?> dependentClass) {
        this.dependentClass = dependentClass;
    }

    public boolean isPkDependency() {
        return isPkDependency;
    }

    public void setPkDependency(boolean isPkDependency) {
        this.isPkDependency = isPkDependency;
    }
}
