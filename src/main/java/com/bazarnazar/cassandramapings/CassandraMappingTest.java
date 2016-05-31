package com.bazarnazar.cassandramapings;

import com.bazarnazar.cassandramapings.context.IContextConfiguration;
import com.bazarnazar.cassandramapings.context.ImportPolicy;
import com.bazarnazar.cassandramapings.context.ValidationPolicy;
import com.bazarnazar.cassandramapings.context.impl.AContextConfiguration;
import com.bazarnazar.cassandramapings.context.impl.CassandraContext;
import com.bazarnazar.cassandramapings.model.User;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQuery;
import com.bazarnazar.cassandramapings.querybuilder.SafeQueryBuilder;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import java.util.UUID;

/**
 * Created by Bazar on 25.05.16.
 */
public class CassandraMappingTest {

    public static void main(String[] args) {
        IContextConfiguration contextConfiguration = new MyContextConfiguration();
        CassandraContext.getInstance().init(contextConfiguration);

        ISafeSelectQuery<User> userSafeSelectQuery = SafeQueryBuilder.select(User.class)
                                                                     .where(User::getUserId)
                                                                     .eq(UUID.fromString(
                                                                             "9c60e693-b60c-4716-bccd-bfe1da1a98f0"));
        Mapper<User> userMapper = new MappingManager(contextConfiguration.getSession())
                .mapper(User.class);
        for (User user : userMapper
                .map(contextConfiguration.getSession().execute(userSafeSelectQuery.build()))) {
            System.out.println(user);
        }

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
