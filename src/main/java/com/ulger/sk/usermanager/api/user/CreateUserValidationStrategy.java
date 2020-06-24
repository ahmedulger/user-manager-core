package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.manager.UserModificationData;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserValidationStrategy extends AbstractUserValidationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CreateUserValidationStrategy.class);

    public CreateUserValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        super(emailValidator, passwordPolicyManager);
    }

    @Override
    public UserValidationResult validate(UserModificationData userModificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating for creation :: request={}", userModificationData);
        }

        Validator validator = new Validator(userModificationData);
        validator.validateEmailAddress();
        validator.validateUserName();
        validator.validateAllPassword();

        return validator.getValidationResult();
    }
}