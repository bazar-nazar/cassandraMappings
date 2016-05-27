package com.bazarnazar.cassandramapings.exceptions;

/**
 * Created by Bazar on 25.05.16.
 */
public class CassandraInitializationException extends RuntimeException {

    public CassandraInitializationException() {
    }

    public CassandraInitializationException(String message) {
        super(message);
    }

    public CassandraInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CassandraInitializationException(Throwable cause) {
        super(cause);
    }

    public CassandraInitializationException(String message,
                                            Throwable cause,
                                            boolean enableSuppression,
                                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
