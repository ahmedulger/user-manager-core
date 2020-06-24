package com.ulger.sk.usermanager.api.user.manager;

/**
 * This interface used to create UserEntity
 */
public interface UserEntityBuilder {

    UserEntity build(Object id, String email, String firstName, String lastName, String credential);

    UserEntity build(UserModificationData data);
}