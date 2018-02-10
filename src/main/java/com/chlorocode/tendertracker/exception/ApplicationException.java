package com.chlorocode.tendertracker.exception;

/**
 * Exception for business logic validation purpose.
 * This exception extends standard RuntimeException.
 */
public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

}
