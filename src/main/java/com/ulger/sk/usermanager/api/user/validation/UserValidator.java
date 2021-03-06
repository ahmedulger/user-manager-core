package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.UserModificationData;

/**
 * The implementations of this interface are responsible for user validation operations
 * Validations should conceived according to expectations about how user properties should be.
 * In other word, looks for if parameters are acceptable or not
 */
public interface UserValidator {

    /**
     * Validates {@link UserModificationData} for creation operation
     * @param userModificationData {@link UserModificationData}
     * @return UserValidationResult that contains validation operation result
     * @throws IllegalArgumentException when given data is null
     */
    UserValidationResult validate(UserModificationData userModificationData);
}