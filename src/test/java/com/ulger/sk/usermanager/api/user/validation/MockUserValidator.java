package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserModificationData;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import org.apache.commons.validator.routines.EmailValidator;

public class MockUserValidator implements UserValidator {

    ValidationHelper validationHelper;
    EmailValidator emailValidator;
    PasswordPolicyManager passwordPolicyManager;

    public MockUserValidator(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
    }

    @Override
    public UserValidationResult validate(UserModificationData userModificationData) {
        validationHelper = new ValidationHelper(emailValidator, passwordPolicyManager, new DefaultI18NHelper(), userModificationData);
        validationHelper.validateUsername();
        validationHelper.validateEmailAddress();
        validationHelper.validateFullName();
        validationHelper.validatePassword();
        validationHelper.validatePasswordPolicy();

        return validationHelper.getValidationResult();
    }
}