package com.bazarnazar.cassandramapings;

import com.bazarnazar.cassandramapings.context.ImportPolicy;
import com.bazarnazar.cassandramapings.context.ValidationPolicy;
import com.bazarnazar.cassandramapings.context.impl.AContextConfiguration;
import com.bazarnazar.cassandramapings.context.impl.CassandraContext;

/**
 * Created by Bazar on 25.05.16.
 */
public class CassandraMappingTest {

    public static void main(String[] args) {
        CassandraContext.getInstance().init(new MyContextConfiguration());
        CassandraContext.getInstance().stop();
    }

    public static class MyContextConfiguration extends AContextConfiguration {
        @Override
        public void confugure() {
            setKeyspace("cassandra_mappings");
            addContactPoint("10.211.55.5");
            setValidationPolicy(ValidationPolicy.NONE);
            setImportPolicy(ImportPolicy.REPLACE);
        }
    }

}
