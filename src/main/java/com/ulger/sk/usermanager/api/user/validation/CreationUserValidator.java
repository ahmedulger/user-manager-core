package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserModificationData;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreationUserValidator implements UserValidator {

    private static final Logger logger = LoggerFactory.getLogger(CreationUserValidator.class);

    private EmailValidator emailValidator;
    private PasswordPolicyManager passwordPolicyManager;
    private I18NHelper i18NHelper;

    public CreationUserValidator(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this(emailValidator, passwordPolicyManager, new DefaultI18NHelper());
    }

    public CreationUserValidator(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager, I18NHelper i18NHelper) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
        this.i18NHelper = i18NHelper;
    }

    @Override
    public UserValidationResult validate(UserModificationData modificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating for creation :: modificationData={}", modificationData);
        }

        ValidationHelper validationHelper = new ValidationHelper(emailValidator, passwordPolicyManager, i18NHelper, modificationData);
        validationHelper.validateUsername();
        validationHelper.validateEmailAddress();
        validationHelper.validateFullName();
        validationHelper.validateAllPassword();

        return validationHelper.getValidationResult();
    }
}