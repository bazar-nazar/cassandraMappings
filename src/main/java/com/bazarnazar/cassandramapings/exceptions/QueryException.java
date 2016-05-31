package com.bazarnazar.cassandramapings.exceptions;

/**
 * Created by Bazar on 31.05.16.
 */
public class QueryException extends RuntimeException {

    public QueryException() {
    }

    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryException(Throwable cause) {
        super(cause);
    }

    public QueryException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
