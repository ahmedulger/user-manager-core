package com.ulger.sk.usermanager.api.user.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class DefaultUser implements User {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String credential;

    private Integer hashCode;

    public static User newInstance(String username, String email, String firstName, String lastName, String credential) {
        return Builder.anUserImp()
                .withUsername(username)
                .withEmail(email)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withCredential(credential)
                .build();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getCredential() {
        return credential;
    }

    @Override
    public int hashCode() {
        if (hashCode != null) {
            return hashCode;
        }

        hashCode = Objects.hash(username);
        return hashCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("username", username)
                .append("email", email)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("credential", credential)
                .toString();
    }

    public static final class Builder {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String credential;

        private Builder() {
        }

        public static Builder anUserImp() {
            return new Builder();
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withCredential(String credential) {
            this.credential = credential;
            return this;
        }

        public DefaultUser build() {
            DefaultUser user = new DefaultUser();
            user.firstName = this.firstName;
            user.credential = this.credential;
            user.lastName = this.lastName;
            user.username = this.username;
            user.email = this.email;
            return user;
        }
    }
}