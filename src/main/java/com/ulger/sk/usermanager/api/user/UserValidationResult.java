package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.apiresult.ErrorCollection;
import com.ulger.sk.usermanager.apiresult.ServiceResultImpl;
import com.ulger.sk.usermanager.apiresult.SimpleErrorCollection;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserValidationResult extends ServiceResultImpl {

    public UserValidationResult() {
        super(new SimpleErrorCollection());
    }

    public UserValidationResult(ErrorCollection errorCollection) {
        super(errorCollection);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("errorCollection", getErrorCollection())
                .toString();
    }
}