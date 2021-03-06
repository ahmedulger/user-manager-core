package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.validation.ChangingPasswordValidator;
import com.ulger.sk.usermanager.api.user.validation.CreationUserValidator;
import com.ulger.sk.usermanager.api.user.validation.UpdatingUserValidator;
import com.ulger.sk.usermanager.api.user.validation.UserValidationContext;
import org.apache.commons.validator.routines.EmailValidator;

public class UserValidationContextInitializer {

    private UserValidationContextInitializer() {
    }

    public static UserValidationContext getDefault(PasswordPolicyManager passwordPolicyManager) {
        return new UserValidationContext()
            .setValidationStrategy(UserOperation.CREATE, new CreationUserValidator(EmailValidator.getInstance(), passwordPolicyManager))
            .setValidationStrategy(UserOperation.UPDATE, new UpdatingUserValidator(EmailValidator.getInstance(), passwordPolicyManager))
            .setValidationStrategy(UserOperation.CHANGE_PASSWORD, new ChangingPasswordValidator(passwordPolicyManager));
    }
}