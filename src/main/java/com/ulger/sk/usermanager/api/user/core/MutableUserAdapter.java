package com.ulger.sk.usermanager.api.user.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

/**
 * This class holds user data to update or save user.
 */
public class MutableUserAdapter implements UserModificationData, User {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String rawPassword;
    private String hashPassword;

    MutableUserAdapter() {
    }

    MutableUserAdapter(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.hashPassword = user.getCredential();
    }

    MutableUserAdapter(UserModificationData modificationData) {
        this.username = modificationData.getUsername();
        this.email = modificationData.getEmail();
        this.firstName = modificationData.getFirstName();
        this.lastName = modificationData.getLastName();
        this.rawPassword = modificationData.getRawPassword();
    }

    @Override
    public String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getRawPassword() {
        return rawPassword;
    }

    void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public String getCredential() {
        return getHashPassword();
    }

    public String getHashPassword() {
        return hashPassword;
    }

    void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableUserAdapter that = (MutableUserAdapter) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(rawPassword, that.rawPassword) &&
                Objects.equals(hashPassword, that.hashPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, firstName, lastName, rawPassword, hashPassword);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("username", username)
                .append("email", email)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("rawPassword", rawPassword)
                .append("hashPassword", hashPassword)
                .toString();
    }
}