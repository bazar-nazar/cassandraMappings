package com.bazarnazar.cassandramapings.model;

import com.bazarnazar.cassandramapings.annotations.DependentTables;
import com.bazarnazar.cassandramapings.model.joins.VideoByUser;
import com.datastax.driver.mapping.annotations.Computed;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Bazar on 27.05.16.
 */
@Table(name = "user")
@DependentTables({VideoByUser.class})
public class User {

    @PartitionKey
    @Computed("uuid()")
    private UUID userId;

    private String firstName;

    private String lastName;

    private String email;

    private Date dateOfBirth;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;

        User user = (User) o;

        if (userId != null ? !userId.equals(user.userId) : user.userId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        return result;
    }

}
