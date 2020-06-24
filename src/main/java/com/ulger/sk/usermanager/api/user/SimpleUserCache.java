package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.model.User;
import com.ulger.sk.usermanager.cache.Cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ulger.sk.usermanager.SkAssertions.notNull;

public class SimpleUserCache implements Cache<User> {

    private Map<Object, User> cache;

    public SimpleUserCache() {
        this.cache = new HashMap<>();
    }

    public SimpleUserCache(Map<Object, User> cache) {
        this.cache = cache;
    }

    @Override
    public void add(User user) {
        notNull(user.getId());
        cache.put(user.getId(), user);
    }

    @Override
    public void add(Collection<User> values) {
        Map<Object, User> newValues = values
                .stream()
                .collect(Collectors.toMap(user -> user.getId(), user -> user));

        cache.putAll(newValues);
    }

    @Override
    public User get(Object id) {
        return cache.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return cache.values();
    }
}