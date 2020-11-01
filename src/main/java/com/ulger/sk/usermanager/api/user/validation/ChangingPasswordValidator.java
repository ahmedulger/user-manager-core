package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserModificationData;
import com.ulger.sk.usermanager.api.user.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangingPasswordValidator implements UserValidator {

    private static final Logger logger = LoggerFactory.getLogger(ChangingPasswordValidator.class);

    private PasswordPolicyManager passwordPolicyManager;
    private I18NHelper i18NHelper;

    public ChangingPasswordValidator(PasswordPolicyManager passwordPolicyManager) {
        this(passwordPolicyManager, new DefaultI18NHelper());
    }

    public ChangingPasswordValidator(PasswordPolicyManager passwordPolicyManager, I18NHelper i18NHelper) {
        this.passwordPolicyManager = passwordPolicyManager;
        this.i18NHelper = i18NHelper;
    }

    @Override
    public UserValidationResult validate(UserModificationData modificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating to change password :: request={}", modificationData);
        }

        ValidationHelper validationHelper = new ValidationHelper(null, passwordPolicyManager, i18NHelper, modificationData);
        validationHelper.validateAllPassword();

        return validationHelper.getValidationResult();
    }
}