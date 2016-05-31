package com.bazarnazar.cassandramapings;

import com.bazarnazar.cassandramapings.model.User;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;

/**
 * Created by Bazar on 23.05.16.
 */
public class CassandraTest {

    public static void main(String[] args) {
        Cluster cluster = Cluster.builder().addContactPoint("10.211.55.5")
                                 .withQueryOptions(new QueryOptions().setFetchSize(1)).build();
        Session session = cluster.connect("cassandra_mappings");
        //        ResultSet resultSet = session.execute("select * from users");
        //        for (Row row : resultSet) {
        //            System.out.println(row.getUUID("user_id") + " " + row.getString
        // ("first_name") + " " +
        //                                       row.getString("last_name"));
        //        }
        //        Statement select = QueryBuilder.select().all().from("mytestkeyspace", "users");
        //        resultSet = session.execute(select);
        //        for (Row row : resultSet) {
        //            System.out.println(row.getUUID("user_id") + " " + row.getString
        // ("first_name") + " " +
        //                                       row.getString("last_name"));
        //        }

        //        Mapper<FooEntity> mapper = new MappingManager(session).mapper(FooEntity.class);
        //        TableMetadata tableMetadata = mapper.getTableMetadata();
        //        System.out.println(tableMetadata.exportAsString());
        //        BatchStatement batchStatement = new BatchStatement();
        //        batchStatement.add(select);

//        QueryBuilder.select().all().from("").
        MappingManager mappingManager = new MappingManager(session);
        Mapper<User> mapper = mappingManager.mapper(User.class);
        UserAccessor userAccessor = mappingManager.createAccessor(UserAccessor.class);
        Result<User> users = mapper.map(session.execute(userAccessor.getAllUsers()));
        System.out.println(users.iterator().next());
        PagingState pagingState = users.getExecutionInfo().getPagingState();
        users = mapper.map(session.execute(userAccessor.getAllUsers().setPagingState(pagingState)));
        System.out.println(users.iterator().next());

        //        for (User user : userAccessor.getAllUsers()) {
        //            System.out.println(user);
        //        }
        //        Mapper<Video> mapper = new MappingManager(session).mapper(Video.class);
        //        ResultSet results = session.execute("SELECT * FROM user");
        //        Result<Video> users = mapper.map(results);
        //        users.getExecutionInfo().
        //        for (Video u : users) {
        //            System.out.println("User : " + u.getUserId());
        //        }

        session.close();
        cluster.close();
        //        cluster.close();
    }

}
