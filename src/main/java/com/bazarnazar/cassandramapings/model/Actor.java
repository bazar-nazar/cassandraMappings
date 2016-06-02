package com.bazarnazar.cassandramapings.model;

import com.bazarnazar.cassandramapings.annotations.DependentTables;
import com.bazarnazar.cassandramapings.annotations.Generated;
import com.bazarnazar.cassandramapings.annotations.GeneratorType;
import com.bazarnazar.cassandramapings.model.joins.VideoByActor;
import com.datastax.driver.mapping.annotations.Computed;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

/**
 * Created by Bazar on 27.05.16.
 */
@Table(name = "actor")
@DependentTables({VideoByActor.class})
public class Actor {

    @PartitionKey
    @Generated(GeneratorType.UUID)
    private UUID actorId;

    private String firstName;

    private String lastName;

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
