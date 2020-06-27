package com.ulger.sk.usermanager.exception;

public class IllegalParameterException extends IllegalArgumentException {

    public IllegalParameterException(String s) {
        super(s);
    }

    public IllegalParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}