package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.UserValidationResult;

/**
 * The implementations of this interface are responsible for user validation operations
 * Validations should conceived according to expectations about how user properties should be.
 * In other word, looks for if parameters are acceptable or not
 */
public interface UserValidationStrategy {

    /**
     * Validates {@link UserModificationData} for creation operation
     * @param userModificationData {@link UserModificationData}
     * @return UserValidationResult that contains validation operation result
     * @throws IllegalArgumentException when given data is null
     */
    UserValidationResult validate(UserModificationData userModificationData);
}