package com.ulger.sk.usermanager.api.user.manager;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

/**
 * This class holds user data to update or save user.
 */
public class MutableUserModificationData implements UserModificationData {

    private Object id;
    private String email;
    private String firstName;
    private String lastName;
    private String rawPassword;
    private String confirmPassword;
    private String hashPassword;

    MutableUserModificationData() {
    }

    MutableUserModificationData(UserModificationData userModificationData) {
        this.id = userModificationData.getId();
        this.email = userModificationData.getEmail();
        this.firstName = userModificationData.getFirstName();
        this.lastName = userModificationData.getLastName();
        this.rawPassword = userModificationData.getRawPassword();
        this.confirmPassword = userModificationData.getConfirmPassword();
    }

    @Override
    public Object getId() {
        return id;
    }

    void setId(Object id) {
        this.id = id;
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

    @Override
    public String getConfirmPassword() {
        return confirmPassword;
    }

    void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String getCredential() {
        return hashPassword;
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
        MutableUserModificationData that = (MutableUserModificationData) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(email, that.email) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(rawPassword, that.rawPassword) &&
                Objects.equals(hashPassword, that.hashPassword) &&
                Objects.equals(confirmPassword, that.confirmPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, rawPassword, hashPassword, confirmPassword);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("email", email)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("rawPassword", rawPassword)
                .append("hashPassword", hashPassword)
                .append("confirmPassword", confirmPassword)
                .toString();
    }
}