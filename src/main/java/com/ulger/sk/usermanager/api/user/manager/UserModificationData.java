package com.ulger.sk.usermanager.api.user.manager;

public interface UserModificationData {

    Object getId();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getRawPassword();

    String getConfirmPassword();
}