package com.ulger.sk.usermanager.api.user.model;

import com.ulger.sk.usermanager.cache.Cacheable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class UserImp implements User, Cacheable {

    private final Object id;
    private final String email;
    private final String firstName;
    private final String lastName;

    private Integer hashCode;

    private UserImp(Object id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static final UserImp newInstance(Object id, String email, String firstName, String lastName) {
        return new UserImp(id, email, firstName, lastName);
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
}