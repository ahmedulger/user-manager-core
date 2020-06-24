package com.ulger.sk.usermanager.apiresult;

import java.util.Collection;
import java.util.Map;

public interface ErrorCollection {

    void addError(String field, String message);

    void addErrorMessage(String message);

    Collection<String> getErrorMessages();

    void setErrorMessages(Collection<String> errorMessages);

    Map<String, String> getErrors();

    void addErrorCollection(ErrorCollection errors);

    void addErrorMessages(Collection<String> errorMessages);

    void addErrors(Map<String, String> errors);

    boolean hasAnyErrors();
}
