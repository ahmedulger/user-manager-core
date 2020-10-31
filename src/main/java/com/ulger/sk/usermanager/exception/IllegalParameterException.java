package com.ulger.sk.usermanager.exception;

public class IllegalParameterException extends IllegalArgumentException {

    private String parameterName;

    public IllegalParameterException(String parameterName, String message) {
        super(message);
        this.parameterName = parameterName;
    }

    public IllegalParameterException(String parameterName, String message, Throwable cause) {
        super(message, cause);
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }
}