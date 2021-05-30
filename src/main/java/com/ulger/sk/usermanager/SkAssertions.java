package com.ulger.sk.usermanager;

import org.apache.commons.lang3.StringUtils;

public class SkAssertions {

    private SkAssertions() {
    }

    public static void notNull(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Parameter should not be null");
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notBlank(String input) {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException("Parameter should not blank");
        }
    }

    public static void notBlank(String field, String input) {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException(field + " should not blank");
        }
    }

    public static void notBlankByMessage(String input, String message) {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException(message);
        }
    }
}