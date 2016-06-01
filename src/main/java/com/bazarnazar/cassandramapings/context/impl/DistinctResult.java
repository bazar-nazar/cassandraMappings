package com.bazarnazar.cassandramapings.context.impl;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.datastax.driver.mapping.Result;
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

    private Set<T> usedIndexes = new HashSet<>();

    DistinctResult(Result<INDEX> rs, BoundMapperFacade<INDEX, T> boundMapperFacade,
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
    protected Iterator<T> fetch(Iterator<T> dataIterator, Iterator<INDEX> indexIterator) {
        while ((resultBuffer == null || !dataIterator.hasNext()) && indexIterator.hasNext()) {
            T indexVal = boundMapperFacade.map(indexIterator.next());
            if (!usedIndexes.contains(indexVal)) {
                resultBuffer = cassandraManager.<T>query(indexVal);
                dataIterator = resultBuffer.iterator();
                usedIndexes.add(indexVal);
            }
        }
        return dataIterator;
    }
}
