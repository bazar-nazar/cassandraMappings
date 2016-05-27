package com.bazarnazar.cassandramapings;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;

/**
 * Created by Bazar on 23.05.16.
 */
public class CassandraTest {

    public static void main(String[] args) {
        Cluster cluster = Cluster.builder().addContactPoint("10.211.55.5").build();
        Session session = cluster.connect("mytestkeyspace");
        ResultSet resultSet = session.execute("select * from users");
        for (Row row : resultSet) {
            System.out.println(row.getUUID("user_id") + " " + row.getString("first_name") + " " +
                                       row.getString("last_name"));
        }
        Statement select = QueryBuilder.select().all().from("mytestkeyspace", "users");
        resultSet = session.execute(select);
        for (Row row : resultSet) {
            System.out.println(row.getUUID("user_id") + " " + row.getString("first_name") + " " +
                                       row.getString("last_name"));
        }

        //        Mapper<FooEntity> mapper = new MappingManager(session).mapper(FooEntity.class);
        //        TableMetadata tableMetadata = mapper.getTableMetadata();
        //        System.out.println(tableMetadata.exportAsString());
        BatchStatement batchStatement = new BatchStatement();
        batchStatement.add(select);
        session.close();
        cluster.close();
        //        cluster.close();
    }

}
