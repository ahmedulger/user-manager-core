package com.ulger.sk.usermanager.api.user.model;

import com.ulger.sk.usermanager.cache.Cacheable;

/**
 * Represents a person of system
 */
public interface User extends Cacheable {

    /**
     * A unique numeric identifier of User.
     * @return id
     */
    Object getId();

    /**
     * Email of user
     * @return email
     */
    String getEmail();

    /**
     * First name of user
     * @return firstName
     */
    String getFirstName();

    /**
     * Last name of user
     * @return lastName
     */
    String getLastName();

    /**
     * Full name of user
     * @return displayName
     */
    default String getDisplayName() {
        return getFirstName() + " " + getLastName();
    }
}