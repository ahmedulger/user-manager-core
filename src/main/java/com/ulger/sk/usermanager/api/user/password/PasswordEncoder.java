package com.ulger.sk.usermanager.api.user.password;

/**
 * Implementations of this interface are responsible for management of password operations.
 */
public interface PasswordEncoder {

    /**
     * Creates encoded password
     * @param rawPassword
     * @return Encoded password
     */
    String encode(CharSequence rawPassword);

    /**
     * Checks if or not given password matches with already encoded password
     * @param rawPassword
     * @param encodedPassword
     * @return true if given raw password matches with encoded password or else returns false
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);

}