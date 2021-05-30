package com.ulger.sk.usermanager.exception;

public class DataAccessException extends RuntimeException {

    private int reasonCode;

    public int getReasonCode() {
        return reasonCode;
    }

    public DataAccessException(int reasonCode) {
        this.reasonCode = reasonCode;
    }

    public DataAccessException(String message, int reasonCode) {
        super(message);
        this.reasonCode = reasonCode;
    }

    public DataAccessException(String message, Throwable cause, int reasonCode) {
        super(message, cause);
        this.reasonCode = reasonCode;
    }

    public DataAccessException(Throwable cause, int reasonCode) {
        super(cause);
        this.reasonCode = reasonCode;
    }

    public DataAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int reasonCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.reasonCode = reasonCode;
    }

    public DataAccessException() {
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }

    public DataAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}