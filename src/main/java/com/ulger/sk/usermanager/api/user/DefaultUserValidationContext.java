package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.manager.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.ulger.sk.usermanager.SkAssertions.notNull;

public class DefaultUserValidationContext {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserValidationContext.class);

    public static final int OPERATION_CREATE = 1;
    public static final int OPERATION_UPDATE = 2;
    public static final int OPERATION_CHANGE_PASSWORD = 3;

    private EmailValidator emailValidator;
    private Map<Integer, UserValidationStrategy> validationStrategies;

    public DefaultUserValidationContext(PasswordPolicyManager passwordPolicyManager) {
        this.emailValidator = EmailValidator.getInstance();
        this.validationStrategies = new HashMap<>();
        this.validationStrategies.put(OPERATION_CREATE, new CreateUserValidationStrategy(emailValidator, passwordPolicyManager));
        this.validationStrategies.put(OPERATION_UPDATE, new UpdateUserValidationStrategy(emailValidator, passwordPolicyManager));
        this.validationStrategies.put(OPERATION_CHANGE_PASSWORD, new ChangePasswordValidationStrategy(passwordPolicyManager));

        logger.info("[DefaultUserValidationContext] UserValidationContext has loaded successfully :: context={}", this);
    }

    public DefaultUserValidationContext(Map<Integer, UserValidationStrategy> validationStrategies) {
        this.validationStrategies = validationStrategies;
        logger.info("[DefaultUserValidationContext] UserValidationContext has loaded successfully :: context={}", this);
    }

    public UserValidationResult validate(UserModificationData userModificationData, Integer operationId) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] Validation operation started :: userData={}, operationId={}", userModificationData, operationId);
        }

        UserValidationStrategy strategy = validationStrategies.get(operationId);
        if (strategy == null) {
            logger.info("[validate] No validation strategy found with operationId :: operationId={}", operationId);
            throw new IllegalArgumentException("No validator found with operationId :: operationId=" + operationId);
        }

        return strategy.validate(userModificationData);
    }

    public UserValidationResult validate(UserModificationData userModificationData, UserValidationStrategy strategy) {
        notNull(strategy);
        return strategy.validate(userModificationData);
    }

    public void addValidationStrategy(Integer operation, UserValidationStrategy strategy) {
        if (validationStrategies.get(operation) != null) {
            logger.info("[addValidationStrategy] Validation strategy found with operation. Strategy will be overridden :: operation={}, validationStrategy={}", operation, strategy);
        }

        validationStrategies.put(operation, strategy);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("OPERATION_CREATE", OPERATION_CREATE)
                .append("OPERATION_UPDATE", OPERATION_UPDATE)
                .append("OPERATION_CHANGE_PASSWORD", OPERATION_CHANGE_PASSWORD)
                .append("emailValidator", emailValidator)
                .append("validationStrategies", validationStrategies)
                .toString();
    }
}