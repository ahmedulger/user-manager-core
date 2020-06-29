package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.manager.UserModificationData;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswordValidationStrategy extends AbstractUserValidationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordValidationStrategy.class);

    public ChangePasswordValidationStrategy(EmailValidator emailValidator, PasswordPolicyManager passwordPolicyManager) {
        super(emailValidator, passwordPolicyManager);
    }

    @Override
    public UserValidationResult validate(UserModificationData userModificationData) {
        if (logger.isDebugEnabled()) {
            logger.debug("[validate] User request is validating to change password :: request={}", userModificationData);
        }

        Validator validator = createValidationHepler(userModificationData);
        validator.validateAllPassword();

        return validator.getValidationResult();
    }
}