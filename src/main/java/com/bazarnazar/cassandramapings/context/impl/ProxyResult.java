package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.exceptions.QueryException;
import com.bazarnazar.cassandramapings.util.CassandraManagerUtil;
import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.mapping.Result;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Bazar on 01.06.16.
 */
public class ProxyResult<T> implements Iterable<T> {

    private final Result<T> rs;

    public ProxyResult(Result<T> rs) {
        this.rs = rs;
    }

    private T proxy(T entity) {
        try {
            return CassandraManagerUtil.proxyEntity(entity);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                IllegalAccessException e) {
            throw new QueryException("Unable to proxy objects of class " + entity.getClass(), e);
        }
    }

    private static boolean shouldSetValue(Object value) {
        if (value == null)
            return false;
        if (value instanceof Collection)
            return !((Collection) value).isEmpty();
        if (value instanceof Map)
            return !((Map) value).isEmpty();
        return true;
    }

    /**
     * Test whether this mapped result set has more results.
     *
     * @return whether this mapped result set has more results.
     */
    public boolean isExhausted() {
        return rs.isExhausted();
    }

    /**
     * Returns the next result (i.e. the entity corresponding to the next row
     * in the result set).
     *
     * @return the next result in this mapped result set or null if it is exhausted.
     */
    public T one() {
        T entity = rs.one();
        return entity == null ? null : proxy(entity);
    }

    /**
     * Returns all the remaining results (entities) in this mapped result set
     * as a list.
     *
     * @return a list containing the remaining results of this mapped result
     * set. The returned list is empty if and only the result set is exhausted.
     */
    public List<T> all() {
        List<T> entities = rs.all();
        List<T> proxies = new ArrayList<T>(entities.size());
        for (T entity : entities) {
            proxies.add(proxy(entity));
        }
        return proxies;
    }

    /**
     * An iterator over the entities of this mapped result set.
     * <p>
     * The {@link Iterator#next} method is equivalent to calling {@link #one}.
     * So this iterator will consume results and after a full iteration, the
     * mapped result set (and underlying {@code ResultSet}) will be empty.
     * <p>
     * The returned iterator does not support the {@link Iterator#remove} method.
     *
     * @return an iterator that will consume and return the remaining rows of
     * this mapped result set.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private final Iterator<T> rowIterator = rs.iterator();

            @Override
            public boolean hasNext() {
                return rowIterator.hasNext();
            }

            @Override
            public T next() {
                return proxy(rowIterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns information on the execution of this query.
     * <p>
     * The returned object includes basic information such as the queried hosts,
     * but also the Cassandra query trace if tracing was enabled for the query.
     *
     * @return the execution info for this query.
     */
    public ExecutionInfo getExecutionInfo() {
        return rs.getExecutionInfo();
    }

}
