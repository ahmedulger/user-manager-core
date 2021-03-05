package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserModificationData;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatingUserValidator implements UserValidator {

    private static final Logger logger = LoggerFactory.getLogger(UpdatingUserValidator.class);

    private EmailValidator emailValidator;
    private PasswordPolicyManager passwordPolicyManager;

    public UpdatingUserValidator(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
    }

    @Override
    public UserValidationResult validate(UserModificationData modificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating to update :: request={}", modificationData);
        }

        ValidationHelper validationHelper = new ValidationHelper(emailValidator, passwordPolicyManager, modificationData);
        validationHelper.validateUsername();
        validationHelper.validateEmailAddress();
        validationHelper.validateFullName();

        return validationHelper.getValidationResult();
    }

}