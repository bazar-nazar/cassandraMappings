package com.bazarnazar.cassandramapings.annotations;

import com.datastax.driver.core.ClusteringOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Bazar on 25.05.16.
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Order {

    ClusteringOrder order() default ClusteringOrder.ASC;

}
