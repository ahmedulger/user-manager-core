package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.PasswordCheckingResult;
import com.ulger.sk.usermanager.api.user.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.UserValidationResult;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ulger.sk.usermanager.SkAssertions.notNull;

class ValidationHelper {

    private static final Logger logger = LoggerFactory.getLogger(ValidationHelper.class);

    protected EmailValidator emailValidator;
    protected PasswordPolicyManager passwordPolicyManager;
    protected I18NHelper i18NHelper;

    protected UserModificationData modificationData;
    protected UserValidationResult validationResult;

    ValidationHelper(
            EmailValidator emailValidator,
            PasswordPolicyManager passwordPolicyManager,
            I18NHelper i18NHelper,
            UserModificationData modificationData) {

        notNull(modificationData);
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
        this.i18NHelper = i18NHelper;
        this.modificationData = modificationData;
        this.validationResult = new UserValidationResult();
    }

    final void addError(UserField field, String message) {
        validationResult.getErrorCollection().addError(field.getName(), message);

        if (logger.isDebugEnabled()) {
            logger.debug("[addError] Field is invalid :: field={}, message={}", field, message);
        }
    }

    UserValidationResult getValidationResult() {
        return validationResult;
    }

    boolean validateEmailAddress() {
        if (StringUtils.isEmpty(modificationData.getEmail())) {
            addError(UserField.EMAIL, i18NHelper.getMessage("validation.email.blank"));
            return false;
        }

        if (!emailValidator.isValid(modificationData.getEmail())) {
            addError(UserField.EMAIL, i18NHelper.getMessage("validation.email.invalid", modificationData.getEmail()));
            return false;
        }

        return true;
    }

    void validateAllPassword() {
        validatePassword();
        validatePasswordPolicy();
    }

    boolean validatePassword() {
        if (StringUtils.isEmpty(modificationData.getRawPassword())) {
            addError(UserField.PASSWORD, i18NHelper.getMessage("validation.password.blank"));
            return false;
        }

        return true;
    }

    boolean validatePasswordPolicy() {
        String password = modificationData.getRawPassword();
        PasswordCheckingResult passwordCheckingResult = passwordPolicyManager.checkPolicy(password);
        if (passwordCheckingResult.hasError()) {
            addError(UserField.PASSWORD, i18NHelper.getMessage("validation.password.invalid"));
            return false;
        }

        return true;
    }

    boolean validateUserName() {
        String firstName = modificationData.getFirstName();
        String lastName = modificationData.getLastName();

        if (StringUtils.isBlank(firstName)) {
            addError(UserField.FIRST_NAME, i18NHelper.getMessage("validation.firstname.blank"));
            return false;
        }

        if (StringUtils.isBlank(lastName)) {
            addError(UserField.LAST_NAME, i18NHelper.getMessage("validation.lastname.blank"));
            return false;
        }

        return false;
    }
}