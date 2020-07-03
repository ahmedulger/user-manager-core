package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.UserValidationResult;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordValidationStrategy implements UserValidationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordValidationStrategy.class);

    private PasswordPolicyManager passwordPolicyManager;
    private I18NHelper i18NHelper;

    public ChangePasswordValidationStrategy(PasswordPolicyManager passwordPolicyManager) {
        this(passwordPolicyManager, new DefaultI18NHelper());
    }

    public ChangePasswordValidationStrategy(PasswordPolicyManager passwordPolicyManager, I18NHelper i18NHelper) {
        this.passwordPolicyManager = passwordPolicyManager;
        this.i18NHelper = i18NHelper;
    }

    @Override
    public UserValidationResult validate(UserModificationData userModificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating to change password :: request={}", userModificationData);
        }

        ValidationHelper validationHelper = createValidationHelper(userModificationData);
        validationHelper.validateAllPassword();

        return validationHelper.getValidationResult();
    }

    private ValidationHelper createValidationHelper(UserModificationData modificationData) {
        return new ValidationHelper(null, passwordPolicyManager, i18NHelper, modificationData);
    }
}