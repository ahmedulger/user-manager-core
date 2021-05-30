package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.apiresult.ErrorBag;

public class ValidationException extends RuntimeException {

    private ErrorBag errorBag;

    public ErrorBag getErrorBag() {
        return errorBag;
    }

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ValidationException(ErrorBag errorBag) {
        this.errorBag = errorBag;
    }

    public ValidationException(String message, ErrorBag errorBag) {
        super(message);
        this.errorBag = errorBag;
    }

    public ValidationException(String message, Throwable cause, ErrorBag errorBag) {
        super(message, cause);
        this.errorBag = errorBag;
    }

    public ValidationException(Throwable cause, ErrorBag errorBag) {
        super(cause);
        this.errorBag = errorBag;
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorBag errorBag) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorBag = errorBag;
    }
}