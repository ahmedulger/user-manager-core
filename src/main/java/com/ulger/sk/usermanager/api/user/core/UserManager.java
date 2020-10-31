package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.validation.ValidationException;

import java.util.List;

/**
 * Simple user operations need to be implemented by implementation of this interface
 */
public interface UserManager {

    /**
     * Returns a {@link User} object whose email equals with given email
     *
     * @param email the email of User
     * @return {@link User} or null if no user found matches with given email
     * @throws IllegalArgumentException if the given email is null or empty
     */
    User getUserByEmail(String email);

    /**
     * Returns all {@link User} from source
     * @return empty list if no user found
     */
    List<User> getAllUsers();

    /**
     * Returns a {@link User} object created by using modification data that is given as parameter
     *
     * @param userModificationData contains user information to be saved.
     * @return Created {@link User} data
     * @throws ValidationException when given data is not valid
     * */
    User createUser(UserModificationData userModificationData);

    /**
     * Returns a {@link User} object updated by using modification data that is given as parameter
     * This method uses id or email parameter to retrieve user from data source. If id or email both null than
     * throws {@link IllegalArgumentException}. Email value can not be updatable when once created.
     *
     * @param username is id of user
     * @param userModificationData contains user information to be saved.
     * @return Updated {@link User} data
     * @throws ValidationException when given data is not valid
     * @throws UserNotFoundException if no user found with given email or id
     */
    User updateUser(String username, UserModificationData userModificationData);

    /**
     * Changes users password
     * @param userModificationData
     * @throws IllegalArgumentException if when given data is null
     * @throws ValidationException if given data is not valid
     * @throws UserNotFoundException if no user found with given email or id
     * @return Updated {@link User} data
     */
    User changePassword(String username, String oldPassword, String newPassword);
}