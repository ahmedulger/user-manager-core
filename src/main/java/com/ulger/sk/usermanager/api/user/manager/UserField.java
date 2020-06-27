package com.ulger.sk.usermanager.api.user.manager;

public enum UserField {
    ID("id"),
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