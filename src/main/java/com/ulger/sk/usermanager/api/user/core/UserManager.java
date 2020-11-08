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
     * @throws UserOperationException when any exception occurred
     * */
    User createUser(UserModificationData userModificationData) throws UserOperationException;

    /**
     * Returns a {@link User} object updated by using modification data that is given as parameter
     * This method uses id or email parameter to retrieve user from data source. If id or email both null than
     * throws {@link IllegalArgumentException}. Email value can not be updatable when once created.
     *
     * @param username is id of user
     * @param userModificationData contains user information to be saved.
     * @throws ValidationException when given data is not valid
     * @throws UserOperationException when any exception occurred
     */
    User updateUser(String username, UserModificationData userModificationData) throws UserOperationException;

    /**
     * Changes users password
     * @param email
     * @param newPassword
     * @param oldPassword
     * @throws IllegalArgumentException if when given data is null
     * @throws ValidationException if given data is not valid
     * @throws UserNotFoundException if no user found with given email or id
     * @throws UserOperationException when any unexpected exception occurred
     */
    void changePassword(String email, String oldPassword, String newPassword) throws UserNotFoundException, UserOperationException;
}