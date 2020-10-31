package com.ulger.sk.usermanager.api.user.core;

import java.util.List;

/**
 * Data source operations like writing, reading
 */
public interface UserDao {

    /**
     * Searches and returns {@link User} whose id is given id value
     *
     * @param username, the id of User
     * @return {@link User}
     */
    User findByUsername(String username);

    /**
     * Searches and returns {@link User} whose email is given email value
     *
     * @param email, the email of User
     * @return {@link User}
     */
    User findByEmail(String email);

    /**
     * Searches and returns all {@link User} list
     *
     * @return empty list if no data found. Returning user list is new list or immutable list.
     */
    List<User> find();

    /**
     * Saves given {@link User} object.
     * If id is given than updates, if not than tries to create new instance
     * @param user
     * @return created instance
     */
    User create(User user);

    /**
     *
     * @param username
     * @param user
     * @return
     */
    User updateByUsername(String username, User user);
}