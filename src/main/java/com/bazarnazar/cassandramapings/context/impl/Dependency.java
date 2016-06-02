package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.util.EntityDefinitionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by Bazar on 01.06.16.
 */
public class Dependency<T> {

    public Dependency(Class<T> dependentClass, String columnName) {
        this.dependentClass = dependentClass;
        isPkDependency = EntityDefinitionUtil.getPrimaryKey(dependentClass).map(t -> t._1)
                                             .anyMatch(c -> c.equals(columnName));
    }

    private Class<T> dependentClass;
    private boolean isPkDependency;

    public Class<T> getDependentClass() {
        return dependentClass;
    }

    public void setDependentClass(Class<T> dependentClass) {
        this.dependentClass = dependentClass;
    }

    public boolean isPkDependency() {
        return isPkDependency;
    }

    public void setPkDependency(boolean isPkDependency) {
        this.isPkDependency = isPkDependency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Dependency))
            return false;

        Dependency that = (Dependency) o;

        if (isPkDependency != that.isPkDependency)
            return false;
        if (dependentClass != null ? !dependentClass
                .equals(that.dependentClass) : that.dependentClass != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dependentClass != null ? dependentClass.hashCode() : 0;
        result = 31 * result + (isPkDependency ? 1 : 0);
        return result;
    }

    public static class CompactDependenciesFilter implements Predicate<Dependency> {

        private Map<Class<?>, Dependency> dependencies = new HashMap<>();

        @Override
        public boolean test(Dependency dependency) {
            if (dependencies.containsKey(dependency.dependentClass)) {
                Dependency storedDependency = dependencies.get(dependency.getDependentClass());
                if (!storedDependency.isPkDependency && dependency.isPkDependency) {
                    storedDependency.isPkDependency = true;
                }
                return false;
            }
            dependencies.put(dependency.getDependentClass(), dependency);
            return true;
        }
    }
}
