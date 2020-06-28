package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.manager.UserField;
import com.ulger.sk.usermanager.api.user.manager.UserModificationData;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
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
    private I18NHelper i18NHelper;

    public AbstractUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        this.emailValidator = emailValidator;
        this.passwordPolicyManager = passwordPolicyManager;
        init();
    }

    public AbstractUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager, I18NHelper i18NHelper) {
        this(emailValidator, passwordPolicyManager);
        this.i18NHelper = i18NHelper;
        init();
    }

    private void init() {
        if (this.i18NHelper == null) {
            logger.warn("[init] I18NHelper implementation not found, initializing with DefaultI18NHelper");
            this.i18NHelper = new DefaultI18NHelper();
        }
    }

    protected final Validator createValidationHepler(UserModificationData userModificationData) {
        return new Validator(userModificationData);
    }

    final class Validator {
        private UserModificationData modificationData;
        private UserValidationResult validationResult;

        Validator(UserModificationData modificationData) {
            notNull(modificationData);
            this.modificationData = modificationData;
            this.validationResult = new UserValidationResult();
        }

        private void addError(String field, Object value, String message) {
            validationResult.getErrorCollection().addError(field, message);

            if (logger.isDebugEnabled()) {
                logger.debug("[addError] Field is invalid :: field={}, value={}, message={}", field, value, message);
            }
        }

        private void addError(UserField field, String message) {
            validationResult.getErrorCollection().addError(field.getName(), message);

            if (logger.isDebugEnabled()) {
                logger.debug("[addError] Field is invalid :: field={}, message={]", field, message);
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("emailValidator", emailValidator)
                .append("passwordPolicyManager", passwordPolicyManager)
                .toString();
    }
}