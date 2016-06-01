package com.bazarnazar.cassandramapings.context.pojo;

import java.util.List;

/**
 * Created by Bazar on 01.06.16.
 */
public interface IPojo {

    Object getStoredObject();

    default List<String> getModifiedColumns() {
        return null;
    }

}
