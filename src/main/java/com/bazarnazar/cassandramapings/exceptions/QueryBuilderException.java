package com.bazarnazar.cassandramapings.exceptions;

/**
 * Created by Bazar on 30.05.16.
 */
public class QueryBuilderException extends RuntimeException {

    public QueryBuilderException() {
    }

    public QueryBuilderException(String message) {
        super(message);
    }

    public QueryBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryBuilderException(Throwable cause) {
        super(cause);
    }

    public QueryBuilderException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
