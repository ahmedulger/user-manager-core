package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.apiresult.ErrorCollection;
import com.ulger.sk.usermanager.apiresult.ErrorCollections;

public class ValidationException extends RuntimeException {

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(ErrorCollection errorCollection) {
        super(ErrorCollections.toString(errorCollection));
    }
}