package com.ulger.sk.usermanager.api.user.core;

public interface UserModificationData {

    String getUsername();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getRawPassword();
}