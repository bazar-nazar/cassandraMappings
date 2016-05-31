package com.bazarnazar.cassandramapings;

import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;



/**
 * Created by Bazar on 30.05.16.
 */
@Accessor
public interface UserAccessor {

    @Query("SELECT * FROM user")
    Statement getAllUsers();

}
