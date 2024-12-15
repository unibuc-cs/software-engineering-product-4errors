package com._errors.MovieMingle.exception;
/*
In case a user's account does not exist in the system for a given email id.
 */
public class UnknownIdentifierException extends Exception{
    public UnknownIdentifierException() {
        super();
    }


    public UnknownIdentifierException(String message) {
        super(message);
    }


    public UnknownIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }
}
