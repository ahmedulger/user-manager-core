package com.ulger.sk.usermanager.api.user.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class AbstractCachingUserManager implements UserManager {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCachingUserManager.class);

    private Map<String, User> userCache;
    private UserManager userManager;

    public AbstractCachingUserManager(UserManager userManager) {
        this.userCache = new HashMap<>();
        this.userManager = userManager;
        init();
    }

    private final void init() {
        if (userManager == null) {
            logger.error("AbstractCachingUserManager requires a none caching user manager]");
            throw new IllegalArgumentException("AbstractCachingUserManager requires a none caching user manager");
        }

        logger.warn("AbstractCachingUserManager is initialized with userManager, " +
                "this user manager needs none caching user manager :: userManager={}", userManager);
    }

    /**
     * This method needs to overridden. Because getUserByEmail method invokes this method first and
     * if no user found than, default user manager's getUserByEmail method invoked.
     * If any user found or not the result object will put on cache.
     * @param email
     * @return
     */
    protected abstract User getUserFromCache(String email);

    /**
     * This method is calling by getUserByEmail, getAllUsers and createOrUpdateUser when user is not found in cache for first time.
     * @param user
     */
    protected abstract void addUserToCache(User user);

    /**
     * This method should return all users in cache. And calling by getAllUsers method.
     * @return list of user, if no user found return empty collection or null
     */
    protected abstract Collection<User> getAllUsersFromCache();

    /**
     * This method refresh single user on cache by query'in data source
     * @param email
     */
    protected abstract void refreshSingleUserOnCache(String email);

    /**
     * Refreshes cache
     */
    protected abstract void refreshCache();

    /**
     * Looks for user in cache by invoking getUserFromCache method. If not found calls {@link UserManager} getUserByEmail
     * method and puts the result to cache by calling addUserToCache
     * @param email the email of User
     * @return User instance from cache or data source
     */
    @Override
    public User getUserByEmail(String email) {
        User user = getUserFromCache(email);
        if (user != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("User with email found in cache :: user={}", user);
            }

            return user;
        }

        // null olma durumunda da cache'e konulabilir
        user = userManager.getUserByEmail(email);
        if (user != null) {
            logger.debug("[getUserByEmail] User with email found in db and putting cache :: email={}, user={}", email, user);
        }

        addUserToCache(user);

        return user;
    }

    /**
     * This method tries to get users from cache by calling getAllUsersFromCache, if no user found then
     * call {@link UserManager} getAllUsers and puts the results to the cache.
     * @return All users from cache or data source and returns new ArrayList with result
     */
    @Override
    public List<User> getAllUsers() {
        Collection<User> usersCollection = getAllUsersFromCache();
        List<User> users = null;

        if (usersCollection != null) {
            users = new ArrayList<>(usersCollection);
        }

        if (users == null || users.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[getAllUsers] No user found in cache");
            }

            users = userManager.getAllUsers();

            if (users.isEmpty()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[getAllUsers] No user found");
                }

                return users;
            }

            users.forEach(this::addUserToCache);
        }

        return users;
    }

    /**
     * Creates user with given data and then puts data to cache. If cache has already
     * user with given email, then overrides data in cache with updated user. After all, triggers events if found.
     * @param modificationData
     * @return
     */
    @Override
    public User createUser(UserModificationData modificationData) {
        User user = userManager.createUser(modificationData);

        if (user != null) {
            addUserToCache(user);

            if (logger.isDebugEnabled()) {
                logger.debug("[createUser] user has been created and put cache :: user={}", user);
            }
        }

        return user;
    }

    /**
     * Updates user with given data and then puts data to cache. If cache has already
     * user with given email, then overrides data in cache with updated user. After all, triggers events if found.
     * @param modificationData
     * @return
     */
    @Override
    public User updateUser(UserModificationData modificationData) {
        User user = userManager.updateUser(modificationData);

        if (user != null) {
            addUserToCache(user);

            if (logger.isDebugEnabled()) {
                logger.debug("[updateUser] user has been updated and put cache :: user={}", user);
            }
        }

        return user;
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        userManager.changePassword(email, oldPassword, newPassword);
        refreshSingleUserOnCache(email);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userCache", userCache)
                .append("userManager", userManager)
                .toString();
    }
}