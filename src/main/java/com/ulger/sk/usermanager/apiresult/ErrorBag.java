package com.ulger.sk.usermanager.apiresult;

import java.util.Collection;
import java.util.Map;

public interface ErrorBag {

    void addError(String field, String message);

    void addErrorMessage(String message);

    Collection<String> getErrorMessages();

    void setErrorMessages(Collection<String> errorMessages);

    Map<String, String> getErrors();

    void addErrorCollection(ErrorBag errors);

    void addErrorMessages(Collection<String> errorMessages);

    void addErrors(Map<String, String> errors);

    boolean hasAnyErrors();
}
