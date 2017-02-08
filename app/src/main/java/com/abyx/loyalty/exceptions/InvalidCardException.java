package com.abyx.loyalty.exceptions;

/**
 * This exception is thrown whenever a Card with an invalid ID is used to update or delete an entry
 * in the database.
 *
 * @author Pieter Verschaffelt
 */
public class InvalidCardException extends RuntimeException {
    public InvalidCardException() {
        super();
    }

    public InvalidCardException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidCardException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InvalidCardException(Throwable throwable) {
        super(throwable);
    }
}
