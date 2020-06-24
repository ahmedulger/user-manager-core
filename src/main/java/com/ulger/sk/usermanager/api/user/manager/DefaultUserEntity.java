package com.ulger.sk.usermanager.api.user.manager;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class DefaultUserEntity implements UserEntity {

    private final Object id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String credential;

    private Integer hashCode;

    public DefaultUserEntity(Object id, String email, String firstName, String lastName, String credential) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.credential = credential;
    }

    public DefaultUserEntity(String firstName, String lastName, String credential) {
        this(null, null, firstName, lastName, credential);
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
        DefaultUserEntity that = (DefaultUserEntity) o;
        return Objects.equals(id, that.id);
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
                .append("credential", credential)
                .append("hashCode", hashCode)
                .toString();
    }
}