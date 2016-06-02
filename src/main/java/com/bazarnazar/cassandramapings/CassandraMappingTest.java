package com.bazarnazar.cassandramapings;

import com.bazarnazar.cassandramapings.context.ICassandraManager;
import com.bazarnazar.cassandramapings.context.IContextConfiguration;
import com.bazarnazar.cassandramapings.context.ImportPolicy;
import com.bazarnazar.cassandramapings.context.ValidationPolicy;
import com.bazarnazar.cassandramapings.context.impl.AContextConfiguration;
import com.bazarnazar.cassandramapings.context.impl.CassandraContext;
import com.bazarnazar.cassandramapings.model.User;
import com.bazarnazar.cassandramapings.model.Video;
import com.bazarnazar.cassandramapings.querybuilder.ISafeSelectQuery;
import com.bazarnazar.cassandramapings.querybuilder.impl.SafeQueryBuilder;
import com.datastax.driver.core.Cluster;

import java.util.UUID;

/**
 * Created by Bazar on 25.05.16.
 */
public class CassandraMappingTest {

    public static void main(String[] args) {
        IContextConfiguration contextConfiguration = new MyContextConfiguration();
        CassandraContext.getInstance().init(contextConfiguration);
        ICassandraManager cassandraManager = CassandraContext.getInstance()
                                                             .createCassandraManager();
        User anyUser = cassandraManager.all(User.class).one();
        anyUser.setEmail("fghjkl");
        anyUser.setLastName("fghjkl");
        //        cassandraManager.save(anyUser);
        //        queryTest(cassandraManager);
        //
        ISafeSelectQuery<Video> videoISafeSelectQuery = SafeQueryBuilder.select(Video.class);
        //
//        for (User user : cassandraManager
//                .queryByDependentTable(videoISafeSelectQuery, User.class)) {
//            System.out.println(user);
//        }

//        videoISafeSelectQuery = SafeQueryBuilder.select(Video.class);

        for (User user : cassandraManager
                .queryDistinctByDependentTable(videoISafeSelectQuery, User.class)) {
            System.out.println(user);
        }


        CassandraContext.getInstance().stop();
    }

    private static void queryTest(ICassandraManager cassandraManager) {
        ISafeSelectQuery<User> userSafeSelectQuery = SafeQueryBuilder.select(User.class)
                                                                     .where(User::getUserId)
                                                                     .eq(UUID.fromString(
                                                                             "9c60e693-b60c-4716-bccd-bfe1da1a98f0"));


        for (User user : cassandraManager.all(User.class)) {
            System.out.println(user);
        }

        User queryObj = new User();
        queryObj.setUserId(UUID.fromString("9c60e693-b60c-4716-bccd-bfe1da1a98f0"));
        for (User user : cassandraManager.query(queryObj)) {
            System.out.println(user);
        }

        for (User user : cassandraManager.query(userSafeSelectQuery)) {
            System.out.println(user);
        }
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
