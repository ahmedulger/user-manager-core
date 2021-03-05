package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserModificationData;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangingPasswordValidator implements UserValidator {

    private static final Logger logger = LoggerFactory.getLogger(ChangingPasswordValidator.class);

    private PasswordPolicyManager passwordPolicyManager;

    public ChangingPasswordValidator(PasswordPolicyManager passwordPolicyManager) {
        this.passwordPolicyManager = passwordPolicyManager;
    }

    @Override
    public UserValidationResult validate(UserModificationData modificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating to change password :: request={}", modificationData);
        }

        ValidationHelper validationHelper = new ValidationHelper(null, passwordPolicyManager, modificationData);
        validationHelper.validateAllPassword();

        return validationHelper.getValidationResult();
    }
}