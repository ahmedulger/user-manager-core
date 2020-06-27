package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.manager.UserModificationData;
import org.apache.commons.validator.routines.EmailValidator;

public class MockUserValidationStrategy extends AbstractUserValidationStrategy {

    public MockUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        super(emailValidator, passwordPolicyManager);
    }

    @Override
    public UserValidationResult validate(UserModificationData userModificationData) {
        Validator validator = new Validator(userModificationData);
        validator.validateEmailAddress();
        validator.validateUserName();
        validator.validatePassword();
        validator.validatePasswordPolicy();

        return validator.getValidationResult();
    }
}