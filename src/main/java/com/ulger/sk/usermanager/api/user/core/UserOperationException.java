package com.ulger.sk.usermanager.api.user.core;

public class UserOperationException extends RuntimeException {

    private int reasonCode;

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

    public UserOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserOperationException(int reasonCode) {
        this.reasonCode = reasonCode;
    }

    public UserOperationException(String message, int reasonCode) {
        super(message);
        this.reasonCode = reasonCode;
    }

    public UserOperationException(String message, Throwable cause, int reasonCode) {
        super(message, cause);
        this.reasonCode = reasonCode;
    }

    public UserOperationException(Throwable cause, int reasonCode) {
        super(cause);
        this.reasonCode = reasonCode;
    }

    public UserOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int reasonCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.reasonCode = reasonCode;
    }
}