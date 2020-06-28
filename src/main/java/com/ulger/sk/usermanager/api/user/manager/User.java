package com.ulger.sk.usermanager.api.user.manager;

/**
 * Represents a person of system
 */
public interface User {

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