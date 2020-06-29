package com.ulger.sk.usermanager.apiresult;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SimpleErrorCollection implements ErrorCollection {

    private Map<String, String> errors;
    private List<String> errorMessages;

    public SimpleErrorCollection() {
        this(Maps.newHashMap(), Lists.<String>newLinkedList());
    }

    public SimpleErrorCollection(ErrorCollection errorCollection) {
        this(Maps.newHashMap(errorCollection.getErrors()), Lists.newLinkedList(errorCollection.getErrorMessages()));
    }

    private SimpleErrorCollection(final Map<String, String> errors, final List<String> errorMessages) {
        this.errors = errors;
        this.errorMessages = errorMessages;
    }

    @Override
    public void addError(String field, String message) {
        errors.put(field, message);
    }

    @Override
    public void addErrorMessage(String message) {
        errorMessages.add(message);
    }

    @Override
    public Collection<String> getErrorMessages() {
        return errorMessages;
    }

    @Override
    public void setErrorMessages(Collection<String> errorMessages) {
        this.errorMessages = new ArrayList<>(errorMessages);
    }

    @Override
    public Map<String, String> getErrors() {
        return errors;
    }

    @Override
    public void addErrorCollection(ErrorCollection errors) {
        addErrorMessages(errors.getErrorMessages());
        addErrors(errors.getErrors());
    }

    @Override
    public void addErrorMessages(Collection<String> incomingMessages) {
        if (incomingMessages == null || incomingMessages.isEmpty()) {
           return;
        }

        for (final String incomingMessage : incomingMessages) {
            addErrorMessage(incomingMessage);
        }
    }

    @Override
    public void addErrors(Map<String, String> incomingErrors) {
        if (incomingErrors == null) {
            return;
        }

        for (final Map.Entry<String, String> mapEntry : incomingErrors.entrySet()) {
            addError(mapEntry.getKey(), mapEntry.getValue());
        }
    }

    @Override
    public boolean hasAnyErrors() {
        return (errors != null && !errors.isEmpty()) || (errorMessages != null && !errorMessages.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleErrorCollection that = (SimpleErrorCollection) o;

        if (!errorMessages.equals(that.errorMessages)) {
            return false;
        }

        if (!errors.equals(that.errors)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = errors.hashCode();
        result = 31 * result + errorMessages.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("errors", errors)
                .append("errorMessages", errorMessages)
                .toString();
    }
}