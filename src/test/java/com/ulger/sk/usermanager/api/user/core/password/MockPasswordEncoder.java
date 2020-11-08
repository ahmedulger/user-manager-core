package com.ulger.sk.usermanager.api.user.core.password;

public class MockPasswordEncoder implements PasswordEncoder {

    private String hashPrefix;

    public MockPasswordEncoder(String hashPrefix) {
        this.hashPrefix = hashPrefix;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return hashPrefix + rawPassword;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return false;
    }
}