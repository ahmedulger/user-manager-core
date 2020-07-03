package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.UserValidationResult;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CreateUserValidationStrategy implements UserValidationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CreateUserValidationStrategy.class);

    private EmailValidator emailValidator;
    private PasswordPolicyManager passwordPolicyManager;
    private I18NHelper i18NHelper;

    public CreateUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this(emailValidator, passwordPolicyManager, new DefaultI18NHelper());
    }

    public CreateUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager, I18NHelper i18NHelper) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
        this.i18NHelper = i18NHelper;
    }

    @Override
    public UserValidationResult validate(UserModificationData userModificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating for creation :: request={}", userModificationData);
        }

        CreateValidationHelper validationHelper = createValidationHelper(userModificationData);
        validationHelper.validateEmailAddress();
        validationHelper.validateUserName();
        validationHelper.validateAllPassword();
        validationHelper.validateId();

        return validationHelper.getValidationResult();
    }

    private CreateValidationHelper createValidationHelper(UserModificationData modificationData) {
        return new CreateValidationHelper(emailValidator, passwordPolicyManager, i18NHelper, modificationData);
    }

    private class CreateValidationHelper extends ValidationHelper {

        CreateValidationHelper(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager, I18NHelper i18NHelper, UserModificationData modificationData) {
            super(emailValidator, passwordPolicyManager, i18NHelper, modificationData);
        }

        boolean validateId() {
            if (!Objects.isNull(modificationData.getId())) {
                addError(UserField.ID, i18NHelper.getMessage("validation.id.notblank"));
                return false;
            }

            return true;
        }
    }
}