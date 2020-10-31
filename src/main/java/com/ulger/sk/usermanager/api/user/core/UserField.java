package com.ulger.sk.usermanager.api.user.core;

public enum UserField {
    USERNAME("username"),
    EMAIL("email"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    PASSWORD("password");

    private String name;

    UserField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}