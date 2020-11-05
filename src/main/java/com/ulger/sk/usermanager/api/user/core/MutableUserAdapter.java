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

    public MutableUserAdapter() {
    }

    public  MutableUserAdapter(String username, UserModificationData modificationData) {
        this(modificationData);
        this.username = username;
    }

    public MutableUserAdapter(UserModificationData modificationData) {
        this.email = modificationData.getEmail();
        this.firstName = modificationData.getFirstName();
        this.lastName = modificationData.getLastName();
        this.rawPassword = modificationData.getRawPassword();
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public String getCredential() {
        return getHashPassword();
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
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
                .append("id", username)
                .append("email", email)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("rawPassword", rawPassword)
                .append("hashPassword", hashPassword)
                .toString();
    }
}