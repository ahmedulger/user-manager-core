package com.ulger.sk.usermanager.apiresult;

public class ErrorCollections {

    private ErrorCollections() {
    }

    public static ErrorCollection of(String message) {
        ErrorCollection errorCollection = new SimpleErrorCollection();
        errorCollection.addErrorMessage(message);

        return errorCollection;
    }

    public static String toString(ErrorCollection errorCollection) {
        return errorCollection.getErrors().values().toString();
    }
}