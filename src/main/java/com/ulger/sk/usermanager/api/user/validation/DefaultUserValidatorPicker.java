package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserOperation;

public class DefaultUserValidatorPicker implements UserValidatorPicker {

    @Override
    public UserValidator pick(UserOperation userOperation) {
        return null;
    }
}