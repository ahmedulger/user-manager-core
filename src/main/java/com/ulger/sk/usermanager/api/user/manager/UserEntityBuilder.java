package com.ulger.sk.usermanager.api.user.manager;

/**
 * This interface used to create UserEntity
 */
public interface UserEntityBuilder {

    User build(Object id, String email, String firstName, String lastName, String credential);

    User build(UserModificationData data);
}