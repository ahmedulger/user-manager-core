package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.UserValidationResult;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import org.apache.commons.validator.routines.EmailValidator;

public class MockUserValidationStrategy implements UserValidationStrategy {

    ValidationHelper validationHelper;
    EmailValidator emailValidator;
    PasswordPolicyManager passwordPolicyManager;

    public MockUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
    }

    @Override
    public UserValidationResult validate(UserModificationData userModificationData) {
        validationHelper = new ValidationHelper(emailValidator, passwordPolicyManager, new DefaultI18NHelper(), userModificationData);
        validationHelper.validateEmailAddress();
        validationHelper.validateUserName();
        validationHelper.validatePassword();
        validationHelper.validatePasswordPolicy();

        return validationHelper.getValidationResult();
    }
}