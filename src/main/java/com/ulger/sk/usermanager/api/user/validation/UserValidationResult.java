package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.apiresult.ErrorBag;
import com.ulger.sk.usermanager.apiresult.SimpleErrorBag;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserValidationResult {

    private ErrorBag errorBag;

    public UserValidationResult() {
        this.errorBag = new SimpleErrorBag();
    }

    public UserValidationResult(ErrorBag errorBag) {
        this.errorBag = errorBag;
    }

    public ErrorBag getErrorBag() {
        return errorBag;
    }

    public boolean isValid() {
        return errorBag != null && errorBag.hasAnyErrors();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("errorBag", errorBag)
                .toString();
    }
}