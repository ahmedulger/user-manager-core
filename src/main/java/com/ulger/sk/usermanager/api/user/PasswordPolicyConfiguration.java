package com.ulger.sk.usermanager.api.user;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This class contains default conditions for password policy
 */
public class PasswordPolicyConfiguration {

    private int minimumLength;
    private int maximumLength;

    public PasswordPolicyConfiguration(int minimumLength, int maximumLength) {
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
    }

    public int getMinimumLength() {
        return minimumLength;
    }

    public int getMaximumLength() {
        return maximumLength;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("minimumLength", minimumLength)
                .append("maximumLength", maximumLength)
                .toString();
    }
}