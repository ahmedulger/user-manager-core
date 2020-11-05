package com.ulger.sk.usermanager.api.user.core.password;

/**
 * This interface defines of password checking steps
 */
public interface PasswordPolicyManager {

    /**
     * This method takes a password and checks if it is eligible for policy conditions
     * @param password Raw password
     * @return PasswordCheckingResult, that contains result of password checking steps
     */
    PasswordCheckingResult checkPolicy(String password);
}