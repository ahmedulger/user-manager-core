package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.exception.DataAccessException;

import java.util.List;

/**
 * Data source operations like writing, reading
 * T generic refers to type of Id field of {@link User}
 */
public interface UserDao<T> {

    /**
     * Searches and returns {@link User} whose id is given id value
     *
     * @param id, the id of User
     * @return {@link User}
     */
    User findById(T id);

    /**
     * Searches and returns {@link User} whose email is given email value
     *
     * @param email, the email of User
     * @return {@link User}
     * @throws DataAccessException when anny error occurred on data source
     */
    User findByEmail(String email);

    /**
     * Searches and returns all {@link User} list
     *
     * @return empty list if no data found. Returning user list is new list or immutable list.
     * @throws DataAccessException when anny error occurred on data source
     */
    List<User> find();

    /**
     * Saves given {@link User} object.
     * If id is given than updates, if not than tries to create new instance
     * @param user
     * @return created or updated instance
     * @throws DataAccessException when anny error occurred on data source
     */
    User save(User user);
}