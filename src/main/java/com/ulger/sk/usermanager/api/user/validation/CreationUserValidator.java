package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserModificationData;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreationUserValidator implements UserValidator {

    private static final Logger logger = LoggerFactory.getLogger(CreationUserValidator.class);

    private EmailValidator emailValidator;
    private PasswordPolicyManager passwordPolicyManager;

    public CreationUserValidator(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
    }

    @Override
    public UserValidationResult validate(UserModificationData modificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating for creation :: modificationData={}", modificationData);
        }

        ValidationHelper validationHelper = new ValidationHelper(emailValidator, passwordPolicyManager, modificationData);
        validationHelper.validateUsername();
        validationHelper.validateEmailAddress();
        validationHelper.validateFullName();
        validationHelper.validateAllPassword();

        return validationHelper.getValidationResult();
    }
}