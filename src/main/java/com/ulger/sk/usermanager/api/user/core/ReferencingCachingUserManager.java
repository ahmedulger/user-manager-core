package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.UserModificationEventListener;
import com.ulger.sk.usermanager.cache.Cache;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ReferencingCachingUserManager extends AbstractCachingUserManager {

    private static final Logger logger = LoggerFactory.getLogger(ReferencingCachingUserManager.class);

    private Cache<User> userCache;

    public ReferencingCachingUserManager(UserManager userManager, Cache<User> userCache) {
        super(userManager);
        this.userCache = userCache;
        init();
    }

    public ReferencingCachingUserManager(UserManager userManager, Cache<User> userCache, Collection<UserModificationEventListener> modificationEventListeners) {
        super(userManager, modificationEventListeners);
        this.userCache = userCache;
        init();
    }

    private void init() {
        if (userCache == null) {
            logger.error("[ReferencingCachingUserManager] requires a user cache manager]");
            throw new IllegalArgumentException("ReferencingCachingUserManager requires a user cache manager");
        }
    }

    @Override
    protected User getUserFromCache(String email) {
        return userCache.get(email);
    }

    @Override
    protected void addUserToCache(User user) {
        userCache.add(user);
    }

    @Override
    protected Collection<User> getAllUsersFromCache() {
        return userCache.getAll();
    }

    @Override
    protected synchronized void refreshCache() {
        try {
            logger.info("[refreshCache] Refreshing cache");
            userCache.add(getAllUsers());
        } catch (Exception e) {
            logger.error("[refreshCache] Unable to refresh cache", e);
        }

        logger.info("[refreshCache] Cache refreshing completed");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userCacheManager", userCache)
                .toString();
    }
}