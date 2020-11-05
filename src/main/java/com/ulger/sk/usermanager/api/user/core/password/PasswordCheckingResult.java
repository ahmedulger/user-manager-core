package com.ulger.sk.usermanager.api.user.core.password;

import java.util.Collection;

/**
 * Defines what results should contain of password check operations
 */
public interface PasswordCheckingResult {

    /**
     * @return true if any error exists
     */
    boolean hasError();

    /**
     * Adds error
     * @param error
     */
    void addError(String error);

    /**
     * Add multiple error at one time
     * @param errors
     */
    void addError(Collection<String> errors);

    /**
     * @return Collection of containing error
     */
    Collection<String> getErrors();

}