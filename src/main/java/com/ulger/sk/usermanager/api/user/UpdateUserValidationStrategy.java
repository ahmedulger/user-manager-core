package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.manager.UserModificationData;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateUserValidationStrategy extends AbstractUserValidationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(UpdateUserValidationStrategy.class);

    public UpdateUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        super(emailValidator, passwordPolicyManager);
    }

    @Override
    public UserValidationResult validate(UserModificationData userModificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating to update :: request={}", userModificationData);
        }

        Validator validator = createValidationHepler(userModificationData);
        validator.validateUserName();

        return validator.getValidationResult();
    }
}