package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.cache.SimpleUserCache;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class SimpleCachingUserManager extends AbstractCachingUserManager {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCachingUserManager.class);

    private SimpleUserCache userCache;

    public SimpleCachingUserManager(UserManager userManager) {
        super(userManager);
        this.userCache = new SimpleUserCache();
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
                .append("userCache", userCache)
                .toString();
    }
}