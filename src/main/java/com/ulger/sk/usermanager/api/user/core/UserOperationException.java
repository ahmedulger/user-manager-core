package com.ulger.sk.usermanager.api.user.core;

public class UserOperationException extends RuntimeException {

    public UserOperationException() {
    }

    public UserOperationException(String message) {
        super(message);
    }

    public UserOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserOperationException(Throwable cause) {
        super(cause);
    }
}