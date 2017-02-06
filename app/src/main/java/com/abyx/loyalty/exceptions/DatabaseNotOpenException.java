package com.abyx.loyalty.exceptions;

/**
 * This exception is used to indicate that a database is trying to be manipulated that isn't open.
 *
 * @author Pieter Verschaffelt
 */
public class DatabaseNotOpenException extends RuntimeException {
    public DatabaseNotOpenException() {
        super();
    }

    public DatabaseNotOpenException(String detailMessage) {
        super(detailMessage);
    }

    public DatabaseNotOpenException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DatabaseNotOpenException(Throwable throwable) {
        super(throwable);
    }
}
