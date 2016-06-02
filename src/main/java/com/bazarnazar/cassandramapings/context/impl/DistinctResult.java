package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import ma.glasnost.orika.BoundMapperFacade;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Bazar on 31.05.16.
 */
public class DistinctResult<T, INDEX> extends ComplexResult<T, INDEX> {

    DistinctResult(ProxyResult<INDEX> rs, BoundMapperFacade<INDEX, T> boundMapperFacade,
            ICassandraManager cassandraManager) {
        super(rs, boundMapperFacade, cassandraManager);
    }

    @Override
    public List<T> all() {
        Set<INDEX> rows = rs.all().stream().collect(Collectors.toSet());
        Set<T> entities = new HashSet<>();
        for (INDEX row : rows) {
            entities.addAll(map(row).all());
        }
        return entities.stream().collect(Collectors.toList());
    }

    @Override
    public Iterator<T> iterator() {
        return new DistinctIterator();
    }

    public class DistinctIterator implements Iterator<T> {

        private final Iterator<INDEX> indexIterator = rs.iterator();
        private Set<T> usedIndexes = new HashSet<>();
        private Set<T> usedData = new HashSet<>();
        private Iterator<T> dataIterator = null;
        private ProxyResult<T> resultBuffer = null;
        private T currentData = null;

        private DistinctIterator() {
            fetch();
        }

        private void fetch() {
            currentData = null;
            while ((resultBuffer == null || currentData == null) && indexIterator.hasNext()) {
                T indexVal = boundMapperFacade.map(indexIterator.next());
                if (!usedIndexes.contains(indexVal)) {
                    resultBuffer = cassandraManager.<T>query(indexVal);
                    dataIterator = resultBuffer.iterator();
                    usedIndexes.add(indexVal);
                    fetchIter();
                }
            }
        }

        private void fetchIter() {
            while (dataIterator.hasNext() && currentData == null) {
                T value = dataIterator.next();
                if (!usedData.contains(value)) {
                    usedData.add(value);
                    currentData = value;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return currentData != null;
        }

        @Override
        public T next() {
            T value = currentData;
            fetch();
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}
