package com.ulger.sk.usermanager.cache;

import java.util.Collection;

public interface Cache<T> {

    void add(T value);

    void add(Collection<T> values);

    T get(Object id);

    Collection<T> getAll();
}