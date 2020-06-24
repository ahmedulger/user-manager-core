package com.ulger.sk.usermanager.apiresult;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ServiceResultImpl implements ServiceResult {

    private final ErrorCollection errorCollection;

    public ServiceResultImpl(ErrorCollection errorCollection) {
        this.errorCollection = errorCollection;
    }

    public boolean isValid() {
        return !errorCollection.hasAnyErrors();
    }

    public ErrorCollection getErrorCollection() {
        return errorCollection;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("errorCollection", errorCollection)
                .toString();
    }
}