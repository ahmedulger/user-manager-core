package com.ulger.sk.usermanager.exception;

import com.ulger.sk.usermanager.apiresult.ErrorCollection;

public class ValidationException extends RuntimeException {

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(ErrorCollection errorCollection) {
    }
}