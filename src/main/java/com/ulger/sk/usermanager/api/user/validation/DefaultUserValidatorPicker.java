package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserOperation;
import com.ulger.sk.usermanager.api.user.core.UserValidationContextInitializer;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;

public class DefaultUserValidatorPicker implements UserValidatorPicker {

    private UserValidationContext userValidationContext;

    public DefaultUserValidatorPicker(PasswordPolicyManager passwordPolicyManager) {
        this.userValidationContext = UserValidationContextInitializer.getDefault(passwordPolicyManager);
    }

    @Override
    public UserValidator pick(UserOperation userOperation) {
        return userValidationContext.getStrategyByOperation(userOperation);
    }
}