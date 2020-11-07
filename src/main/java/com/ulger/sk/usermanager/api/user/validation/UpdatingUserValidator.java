package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserField;
import com.ulger.sk.usermanager.api.user.core.UserModificationData;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatingUserValidator implements UserValidator {

    private static final Logger logger = LoggerFactory.getLogger(UpdatingUserValidator.class);

    private EmailValidator emailValidator;
    private PasswordPolicyManager passwordPolicyManager;
    private I18NHelper i18NHelper;

    public UpdatingUserValidator(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this(emailValidator, passwordPolicyManager, new DefaultI18NHelper());
    }

    public UpdatingUserValidator(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager, I18NHelper i18NHelper) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
        this.i18NHelper = i18NHelper;
    }

    @Override
    public UserValidationResult validate(UserModificationData modificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating to update :: request={}", modificationData);
        }

        UpdateValidationHelper validationHelper = createValidationHelper(modificationData);
        validationHelper.validateUsername();
        validationHelper.validateEmailAddress();
        validationHelper.validateFullName();

        return validationHelper.getValidationResult();
    }

    private UpdateValidationHelper createValidationHelper(UserModificationData modificationData) {
        return new UpdateValidationHelper(emailValidator, passwordPolicyManager, i18NHelper, modificationData);
    }

    private class UpdateValidationHelper extends ValidationHelper {

        UpdateValidationHelper(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager, I18NHelper i18NHelper, UserModificationData modificationData) {
            super(emailValidator, passwordPolicyManager, i18NHelper, modificationData);
        }

        @Override
        boolean validateEmailAddress() {
            if (StringUtils.isBlank(modificationData.getEmail())) {
                addError(UserField.EMAIL, i18NHelper.getMessage("validation.email.notblank"));
                return false;
            }

            return true;
        }
    }
}