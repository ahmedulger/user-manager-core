package com.ulger.sk.usermanager.api.user.password;

/**
 * This class is a step of rule or rules for password policy checking
 */
public interface PasswordPolicyCondition {

    /**
     * Checks password if it obey the rules
     * @param password Raw password input
     * @return PasswordCheckingResult that contains errors
     */
    PasswordCheckingResult check(String password);
}