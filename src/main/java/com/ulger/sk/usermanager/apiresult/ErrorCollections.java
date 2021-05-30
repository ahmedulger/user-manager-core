package com.ulger.sk.usermanager.apiresult;

public class ErrorCollections {

    private ErrorCollections() {
    }

    public static ErrorBag of(String message) {
        ErrorBag errorBag = new SimpleErrorBag();
        errorBag.addErrorMessage(message);

        return errorBag;
    }

    public static String toString(ErrorBag errorBag) {
        return errorBag.getErrors().values().toString();
    }
}