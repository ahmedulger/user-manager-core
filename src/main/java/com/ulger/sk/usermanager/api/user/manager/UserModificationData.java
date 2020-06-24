package com.ulger.sk.usermanager.api.user.manager;

public interface UserModificationData extends UserEntity {

    Object getId();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getRawPassword();

    String getConfirmPassword();
}