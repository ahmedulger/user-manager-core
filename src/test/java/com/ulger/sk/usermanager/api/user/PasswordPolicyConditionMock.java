package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.password.PasswordCheckingResult;
import com.ulger.sk.usermanager.api.user.password.PasswordPolicyCondition;
import com.ulger.sk.usermanager.api.user.password.SimplePasswordCheckingResult;

import java.util.function.BiConsumer;

public class PasswordPolicyConditionMock implements PasswordPolicyCondition {

    private BiConsumer<String, PasswordCheckingResult> checkingConsumer;

    public PasswordPolicyConditionMock(BiConsumer<String, PasswordCheckingResult> checkingConsumer) {
        this.checkingConsumer = checkingConsumer;
    }

    @Override
    public PasswordCheckingResult check(String password) {
        PasswordCheckingResult checkingResult = new SimplePasswordCheckingResult();
        checkingConsumer.accept(password, checkingResult);
        return checkingResult;
    }
}