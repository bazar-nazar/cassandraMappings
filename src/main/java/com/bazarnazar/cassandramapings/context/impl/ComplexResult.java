package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.datastax.driver.core.ExecutionInfo;
import ma.glasnost.orika.BoundMapperFacade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Bazar on 31.05.16.
 */
public class ComplexResult<T, INDEX> implements Iterable<T> {

    protected final ProxyResult<INDEX> rs;
    protected ICassandraManager cassandraManager;
    protected BoundMapperFacade<INDEX, T> boundMapperFacade;
    protected ProxyResult<T> resultBuffer = null;

    ComplexResult(ProxyResult<INDEX> rs, BoundMapperFacade<INDEX, T> boundMapperFacade,
            ICassandraManager cassandraManager) {
        this.rs = rs;
        this.cassandraManager = cassandraManager;
        this.boundMapperFacade = boundMapperFacade;
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
        return row == null ? null : map(row).one();
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
            entities.addAll(map(row).all());
        }
        return entities;
    }

    //    Set<INDEX> rows = rs.all().stream().collect(Collectors.toSet());
    //    Set<T> entities = new HashSet<>();
    //    for (INDEX row : rows) {
    //        entities.addAll(map(row).all());
    //    }
    //    return entities.stream().collect(Collectors.toList());

    protected ProxyResult<T> map(INDEX index) {
        //todo need to be optimized(bucket select)
        T queryObject = boundMapperFacade.map(index);
        return cassandraManager.<T>query(queryObject);
    }


    protected Iterator<T> fetch(Iterator<T> dataIterator, Iterator<INDEX> indexIterator) {
        while ((resultBuffer == null || !dataIterator.hasNext()) && indexIterator.hasNext()) {
            T indexVal = boundMapperFacade.map(indexIterator.next());
            resultBuffer = cassandraManager.<T>query(indexVal);
            dataIterator = resultBuffer.iterator();
        }
        return dataIterator;
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
            private final Iterator<INDEX> indexIterator = rs.iterator();
            private Iterator<T> dataIterator = null;

            @Override
            public boolean hasNext() {
                dataIterator = fetch(dataIterator, indexIterator);
                return (resultBuffer != null && dataIterator.hasNext());
            }

            @Override
            public T next() {
                dataIterator = fetch(dataIterator, indexIterator);
                return dataIterator.next();
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
