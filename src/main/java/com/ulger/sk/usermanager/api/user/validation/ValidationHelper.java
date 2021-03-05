package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserField;
import com.ulger.sk.usermanager.api.user.core.UserModificationData;
import com.ulger.sk.usermanager.api.user.core.password.PasswordCheckingResult;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ulger.sk.usermanager.SkAssertions.notNull;

class ValidationHelper {

    private static final Logger logger = LoggerFactory.getLogger(ValidationHelper.class);

    protected EmailValidator emailValidator;
    protected PasswordPolicyManager passwordPolicyManager;
    protected UserModificationData modificationData;
    protected UserValidationResult validationResult;

    ValidationHelper(
            EmailValidator emailValidator,
            PasswordPolicyManager passwordPolicyManager,
            UserModificationData modificationData) {

        notNull(modificationData);
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
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

    boolean validateUsername() {
        String username = modificationData.getUsername();

        if (StringUtils.isBlank(username)) {
            addError(UserField.USERNAME, "Username must not blank");
            return false;
        }

        return false;
    }

    boolean validateEmailAddress() {
        if (StringUtils.isEmpty(modificationData.getEmail())) {
            addError(UserField.EMAIL, "Email must not blank");
            return false;
        }

        if (!emailValidator.isValid(modificationData.getEmail())) {
            addError(UserField.EMAIL, "Invalid email address");
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
            addError(UserField.PASSWORD, "Passwor must not blank");
            return false;
        }

        return true;
    }

    boolean validatePasswordPolicy() {
        String password = modificationData.getRawPassword();
        PasswordCheckingResult passwordCheckingResult = passwordPolicyManager.checkPolicy(password);
        if (passwordCheckingResult.hasError()) {
            addError(UserField.PASSWORD, "Invalid password");
            return false;
        }

        return true;
    }

    boolean validateFullName() {
        String firstName = modificationData.getFirstName();
        String lastName = modificationData.getLastName();

        if (StringUtils.isBlank(firstName)) {
            addError(UserField.FIRST_NAME, "First name must not blank");
            return false;
        }

        if (StringUtils.isBlank(lastName)) {
            addError(UserField.LAST_NAME, "Last name must not blank");
            return false;
        }

        return false;
    }
}