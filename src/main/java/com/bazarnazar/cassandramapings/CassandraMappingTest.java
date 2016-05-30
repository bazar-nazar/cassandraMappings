package com.bazarnazar.cassandramapings;

import com.bazarnazar.cassandramapings.context.ImportPolicy;
import com.bazarnazar.cassandramapings.context.ValidationPolicy;
import com.bazarnazar.cassandramapings.context.impl.AContextConfiguration;
import com.bazarnazar.cassandramapings.context.impl.CassandraContext;
import com.datastax.driver.core.Cluster;

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
            Cluster cluster = Cluster.builder().addContactPoint("10.211.55.5").build();
            setSession(cluster.connect("cassandra_mappings"));
            setValidationPolicy(ValidationPolicy.DROPCREATE);
            setImportPolicy(ImportPolicy.ADD);
        }
    }

}
