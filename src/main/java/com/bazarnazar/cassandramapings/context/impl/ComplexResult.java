package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.mapping.Result;
import ma.glasnost.orika.BoundMapperFacade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Bazar on 31.05.16.
 */
public class ComplexResult<T, INDEX> implements Iterable<T> {

    private final Result<INDEX> rs;
    private ICassandraManager cassandraManager;
    private BoundMapperFacade<INDEX, T> boundMapperFacade;

    ComplexResult(Result<INDEX> rs, BoundMapperFacade<INDEX, T> boundMapperFacade,
            ICassandraManager cassandraManager) {
        this.rs = rs;
        this.cassandraManager = cassandraManager;
        this.boundMapperFacade = boundMapperFacade;
    }

    private T map(INDEX index) {
        //todo need to be optimized(bucket select)
        T queryObject = boundMapperFacade.map(index);
        return cassandraManager.<T>query(queryObject).one();
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
        INDEX row = rs.one();
        return row == null ? null : map(row);
    }

    /**
     * Returns all the remaining results (entities) in this mapped result set
     * as a list.
     *
     * @return a list containing the remaining results of this mapped result
     * set. The returned list is empty if and only the result set is exhausted.
     */
    public List<T> all() {
        List<INDEX> rows = rs.all();
        List<T> entities = new ArrayList<>(rows.size());
        for (INDEX row : rows) {
            entities.add(map(row));
        }
        return entities;
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
            private final Iterator<INDEX> rowIterator = rs.iterator();

            @Override
            public boolean hasNext() {
                return rowIterator.hasNext();
            }

            @Override
            public T next() {
                return map(rowIterator.next());
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
