package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.exception.DataAccessException;

import java.util.List;

/**
 * Data source operations like writing, reading.
 * The class implementing this interface should throw {@link com.ulger.sk.usermanager.exception.DataAccessException}
 * when any error occurred while accessing data source
 */
public interface UserDao {

    /**
     * Searches and returns {@link User} whose id is given id value
     *
     * @param username, the id of User
     * @throws DataAccessException
     * @return {@link User}
     */
    User findByUsername(String username) throws DataAccessException;

    /**
     * Searches and returns {@link User} whose email is given email value
     *
     * @param email, the email of User
     * @throws DataAccessException
     * @return {@link User}
     */
    User findByEmail(String email) throws DataAccessException;

    /**
     * Searches and returns all {@link User} list
     *
     * @throws DataAccessException
     * @return empty list if no data found. Returning user list is new list or immutable list.
     */
    List<User> find() throws DataAccessException;

    /**
     * Saves given {@link User} object.
     * If id is given than updates, if not than tries to create new instance
     * @param user
     * @throws DataAccessException
     * @return created instance
     */
    User create(User user) throws DataAccessException;

    /**
     *
     * @param username
     * @param user
     * @throws DataAccessException
     * @return
     */
    User updateByUsername(String username, User user) throws DataAccessException;
}