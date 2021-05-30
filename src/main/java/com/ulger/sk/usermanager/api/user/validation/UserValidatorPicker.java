package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserOperation;

public interface UserValidatorPicker {

    UserValidator pick(UserOperation userOperation);
}