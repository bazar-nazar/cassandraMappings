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

    ComplexResult(ProxyResult<INDEX> rs, BoundMapperFacade<INDEX, T> boundMapperFacade,
            ICassandraManager cassandraManager) {
        this.rs = rs;
        this.cassandraManager = cassandraManager;
        this.boundMapperFacade = boundMapperFacade;
    }

    public boolean isExhausted() {
        return rs.isExhausted();
    }

    public T one() {
        INDEX row = rs.one();
        return row == null ? null : map(row).one();
    }

    public List<T> all() {
        List<INDEX> rows = rs.all();
        List<T> entities = new ArrayList<>(rows.size());
        for (INDEX row : rows) {
            entities.addAll(map(row).all());
        }
        return entities;
    }

    protected ProxyResult<T> map(INDEX index) {
        //todo need to be optimized(bucket select)
        T queryObject = boundMapperFacade.map(index);
        return cassandraManager.<T>query(queryObject);
    }


    @Override
    public Iterator<T> iterator() {
        return new ComplexIterator();
    }


    public class ComplexIterator implements Iterator<T> {
        private final Iterator<INDEX> indexIterator = rs.iterator();
        private Iterator<T> dataIterator = null;
        private ProxyResult<T> resultBuffer;

        private ComplexIterator() {
            fetch();
        }

        protected void fetch() {
            while ((resultBuffer == null || !dataIterator.hasNext()) && indexIterator.hasNext()) {
                T indexVal = boundMapperFacade.map(indexIterator.next());
                resultBuffer = cassandraManager.<T>query(indexVal);
                dataIterator = resultBuffer.iterator();
            }
        }

        @Override
        public boolean hasNext() {
            return (resultBuffer != null && (dataIterator.hasNext() || indexIterator.hasNext()));
        }

        @Override
        public T next() {
            fetch();
            return dataIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public ExecutionInfo getExecutionInfo() {
        return rs.getExecutionInfo();
    }

}
