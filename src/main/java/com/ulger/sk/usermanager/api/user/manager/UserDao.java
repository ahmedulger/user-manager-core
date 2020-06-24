package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.exception.DataAccessException;

import java.util.List;

/**
 * Data source operations like writing, reading
 * T generic refers to type of Id field of {@link UserEntity}
 */
public interface UserDao<T> {

    /**
     * Searches and returns {@link UserEntity} whose id is given id value
     *
     * @param id, the id of UserEntity
     * @return {@link UserEntity}
     * @throws DataAccessException when anny error occurred on data source
     */
    UserEntity findById(T id) throws DataAccessException;

    /**
     * Searches and returns {@link UserEntity} whose email is given email value
     *
     * @param email, the email of UserEntity
     * @return {@link UserEntity}
     * @throws DataAccessException when anny error occurred on data source
     */
    UserEntity findByEmail(String email) throws DataAccessException;

    /**
     * Searches and returns all {@link UserEntity} list
     *
     * @return empty list if no data found. List<UserEntity>
     * @throws DataAccessException when anny error occurred on data source
     */
    List<UserEntity> find() throws DataAccessException;

    /**
     * Saves given {@link UserEntity} object.
     * If id is given than updates, if not than tries to create new instance
     * @param userEntity
     * @return created or updated instance
     * @throws DataAccessException when anny error occurred on data source
     */
    UserEntity save(UserEntity userEntity) throws DataAccessException;
}