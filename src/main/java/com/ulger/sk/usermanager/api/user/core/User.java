package com.ulger.sk.usermanager.api.user.core;

/**
 * Represents a person of system
 */
public interface User {

    /**
     * A unique identifier of User.
     * @return username
     */
    String getUsername();

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
     * Credential of user
     * @return credential
     */
    String getCredential();

    /**
     * Full name of user
     * @return displayName
     */
    default String getDisplayName() {
        return getFirstName() + " " + getLastName();
    }
}