package com.ulger.sk.usermanager.api.user.core.password;

public class DefaultPasswordLengthCondition implements PasswordPolicyCondition {

    public static final int DEFAULT_MIN_LENGTH = 8;
    public static final int DEFAULT_MAX_LENGTH = 32;

    private int minLength = DEFAULT_MIN_LENGTH;
    private int maxLength = DEFAULT_MAX_LENGTH;

    public DefaultPasswordLengthCondition() {
    }

    public DefaultPasswordLengthCondition(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public PasswordCheckingResult check(String password) {
        PasswordCheckingResult result = new SimplePasswordCheckingResult();

        if (password.length() < minLength) {
            result.addError("Password should be at least " + minLength + " characters long");
        }

        if (password.length() > maxLength) {
            result.addError("Password can not be longer than " + maxLength + " characters");
        }

        return result;
    }
}