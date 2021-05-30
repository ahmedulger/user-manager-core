package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserOperation;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class UserValidationContext {

    private static final Logger logger = LoggerFactory.getLogger(UserValidationContext.class);

    private EmailValidator emailValidator;
    private Map<UserOperation, UserValidator> validationStrategies;

    public UserValidationContext() {
        this.emailValidator = EmailValidator.getInstance();
        this.validationStrategies = new HashMap<>();

        logger.info("[UserValidationContext] UserValidationContext has loaded successfully :: context={}", this);
    }

    public UserValidator getStrategyByOperation(UserOperation userOperation) {
        return validationStrategies.get(userOperation);
    }

    public UserValidationContext setValidationStrategy(UserOperation operation, UserValidator strategy) {
        if (validationStrategies.get(operation) != null) {
            logger.info("[addValidationStrategy] Validation strategy found with operation. Strategy will be overridden :: operation={}, validationStrategy={}", operation, strategy);
        }

        validationStrategies.put(operation, strategy);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("emailValidator", emailValidator)
                .append("validationStrategies", validationStrategies)
                .toString();
    }
}