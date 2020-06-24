package com.ulger.sk.usermanager;

import org.apache.commons.lang3.StringUtils;

public class SkAssertions {

    private SkAssertions() {
    }

    public static final void notNull(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Parameter should not be null");
        }
    }

    public static final void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static final void notBlank(String field, String input) {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException(field + " should not blank");
        }
    }

    public static final void notBlankByMessage(String input, String message) {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException(message);
        }
    }
}