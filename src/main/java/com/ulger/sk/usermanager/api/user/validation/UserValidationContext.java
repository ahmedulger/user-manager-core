package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.*;
import com.ulger.sk.usermanager.api.user.password.PasswordPolicyManager;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.ulger.sk.usermanager.SkAssertions.notNull;

public class UserValidationContext {

    private static final Logger logger = LoggerFactory.getLogger(UserValidationContext.class);

    private EmailValidator emailValidator;
    private Map<UserOperation, UserValidator> validationStrategies;

    public UserValidationContext(PasswordPolicyManager passwordPolicyManager) {
        this.emailValidator = EmailValidator.getInstance();
        this.validationStrategies = new HashMap<>();
        this.validationStrategies.put(UserOperation.CREATE, new CreateUserValidator(emailValidator, passwordPolicyManager));
        this.validationStrategies.put(UserOperation.UPDATE, new UpdateUserValidator(emailValidator, passwordPolicyManager));
        this.validationStrategies.put(UserOperation.CHANGE_PASSWORD, new ChangePasswordValidator(passwordPolicyManager));

        logger.info("[DefaultUserValidationContext] UserValidationContext has loaded successfully :: context={}", this);
    }

    public UserValidationResult validate(UserModificationData userModificationData, Integer operationId) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] Validation operation started :: userData={}, operationId={}", userModificationData, operationId);
        }

        UserValidator strategy = validationStrategies.get(operationId);
        if (strategy == null) {
            logger.info("[validate] No validation strategy found with operationId :: operationId={}", operationId);
            throw new IllegalArgumentException("No validator found with operationId :: operationId=" + operationId);
        }

        return strategy.validate(userModificationData);
    }

    public UserValidationResult validate(UserModificationData userModificationData, UserValidator strategy) {
        notNull(strategy);
        return strategy.validate(userModificationData);
    }

    public void addValidationStrategy(UserOperation operation, UserValidator strategy) {
        if (validationStrategies.get(operation) != null) {
            logger.info("[addValidationStrategy] Validation strategy found with operation. Strategy will be overridden :: operation={}, validationStrategy={}", operation, strategy);
        }

        validationStrategies.put(operation, strategy);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("emailValidator", emailValidator)
                .append("validationStrategies", validationStrategies)
                .toString();
    }
}