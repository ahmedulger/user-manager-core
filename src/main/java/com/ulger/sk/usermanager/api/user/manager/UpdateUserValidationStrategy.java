package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.UserValidationResult;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class UpdateUserValidationStrategy implements UserValidationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(UpdateUserValidationStrategy.class);

    private EmailValidator emailValidator;
    private PasswordPolicyManager passwordPolicyManager;
    private I18NHelper i18NHelper;

    public UpdateUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this(emailValidator, passwordPolicyManager, new DefaultI18NHelper());
    }

    public UpdateUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager, I18NHelper i18NHelper) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
        this.i18NHelper = i18NHelper;
    }

    @Override
    public UserValidationResult validate(UserModificationData userModificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating to update :: request={}", userModificationData);
        }

        UpdateValidationHelper validationHelper = createValidationHelper(userModificationData);
        validationHelper.validateId();
        validationHelper.validateEmailAddress();

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
            if (!StringUtils.isEmpty(modificationData.getEmail())) {
                addError(UserField.EMAIL, i18NHelper.getMessage("validation.email.notblank"));
                return false;
            }

            return true;
        }

        boolean validateId() {
            if (Objects.isNull(modificationData.getId())) {
                addError(UserField.ID, i18NHelper.getMessage("validation.id.blank"));
                return false;
            }

            return true;
        }
    }
}