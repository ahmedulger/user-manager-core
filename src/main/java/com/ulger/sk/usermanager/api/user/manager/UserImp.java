package com.ulger.sk.usermanager.api.user.manager;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class UserImp implements User {

    private final Object id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String credential;

    private Integer hashCode;

    private UserImp(Object id, String email, String firstName, String lastName, String credential) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.credential = credential;
    }

    public static final UserImp newInstance(Object id, String email, String firstName, String lastName, String credential) {
        return new UserImp(id, email, firstName, lastName, credential);
    }

    @Override
    public Object getId() {
        return id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserImp userImp = (UserImp) o;
        return Objects.equals(id, userImp.id);
    }

    @Override
    public int hashCode() {
        if (hashCode != null) {
            return hashCode;
        }

        return this.hashCode = Objects.hash(id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("email", email)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .toString();
    }

    public static final class Builder {
        private Object id;
        private String email;
        private String firstName;
        private String lastName;
        private String credential;

        private Builder() {
        }

        public static Builder anUserImp() {
            return new Builder();
        }

        public Builder withId(Object id) {
            this.id = id;
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

        public UserImp build() {
            UserImp userImp = new UserImp(id, email, firstName, lastName, credential);
            return userImp;
        }
    }
}