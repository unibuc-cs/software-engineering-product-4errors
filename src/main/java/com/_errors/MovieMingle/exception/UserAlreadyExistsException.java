package com._errors.MovieMingle.exception;
/**
 * Exception thrown by system in case someone tries to register with an already existing email
 * id in the system.
 */
public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException() {
        super();
    }


    public UserAlreadyExistsException(String message) {
        super(message);
    }


    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
