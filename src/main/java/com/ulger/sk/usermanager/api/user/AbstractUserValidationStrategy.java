package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.manager.UserModificationData;
import com.ulger.sk.usermanager.api.user.model.UserFields;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ulger.sk.usermanager.SkAssertions.notNull;

public abstract class AbstractUserValidationStrategy implements UserValidationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(AbstractUserValidationStrategy.class);

    private EmailValidator emailValidator;
    private PasswordPolicyManager passwordPolicyManager;

    public AbstractUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
    }

    protected final Validator createValidationHepler(UserModificationData userModificationData) {
        return new Validator(userModificationData);
    }

    final class Validator {
        private UserModificationData userModificationData;
        private UserValidationResult validationResult;

        Validator(UserModificationData userModificationData) {
            notNull(userModificationData);
            this.userModificationData = userModificationData;
            this.validationResult = new UserValidationResult();
        }

        private void addError(String field, Object value, String message) {
            validationResult.getErrorCollection().addError(field, message);

            if (logger.isDebugEnabled()) {
                logger.debug("[addError] Field is invalid :: field={}, value={}, message={}", field, value, message);
            }
        }

        private void addError(String field, String message) {
            validationResult.getErrorCollection().addError(field, message);

            if (logger.isDebugEnabled()) {
                logger.debug("[addError] Field is invalid :: field={}, message={]", field, message);
            }
        }

        UserValidationResult getValidationResult() {
            return validationResult;
        }

        boolean validateEmailAddress() {
            if (StringUtils.isEmpty(userModificationData.getEmail())) {
                addError(UserFields.EMAIL, "You must specify email address");
                return false;
            }

            if (!emailValidator.isValid(userModificationData.getEmail())) {
                addError(UserFields.EMAIL, "You must specify a valid email address");
                return false;
            }

            return true;
        }

        void validateAllPassword() {
            validatePassword();
            validateConfirmPassword();
            validatePasswordPolicy();
        }

        boolean validatePassword() {
            if (StringUtils.isEmpty(userModificationData.getRawPassword())) {
                addError(UserFields.PASSWORD, "You must specify a password");
                return false;
            }

            return true;
        }

        boolean validateConfirmPassword() {
            String password = userModificationData.getRawPassword();
            String confirmPassword = userModificationData.getConfirmPassword();

            if (StringUtils.isNotEmpty(confirmPassword) || StringUtils.isNotEmpty(password)) {
                if (password == null || !password.equals(confirmPassword)) {
                    addError(UserFields.CONFIRM_PASSWORD, "Your password and confirmation password do not match");
                    return false;
                }
            }

            return true;
        }

        boolean validatePasswordPolicy() {
            String password = userModificationData.getRawPassword();
            PasswordCheckingResult passwordCheckingResult = passwordPolicyManager.checkPolicy(password);
            if (passwordCheckingResult.hasError()) {
                addError(UserFields.PASSWORD, "You must specify a password");
                return false;
            }

            return true;
        }

        boolean validateUserName() {
            String firstName = userModificationData.getFirstName();
            String lastName = userModificationData.getLastName();

            if (StringUtils.isBlank(firstName)) {
                addError(UserFields.FIRST_NAME, "You must specify your name");
                return false;
            }

            if (StringUtils.isBlank(lastName)) {
                addError(UserFields.LAST_NAME, "You must specify your last name");
                return false;
            }

            return false;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("emailValidator", emailValidator)
                .append("passwordPolicyManager", passwordPolicyManager)
                .toString();
    }
}