package com.ulger.sk.usermanager.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SkCollectionUtils {

    private SkCollectionUtils() {
    }

    public static <T, R> Set<R> convertAndCreateSet(Iterable<T> iterable, Function<T, R> mapper) {
        if (!iterable.iterator().hasNext()) {
            return Collections.emptySet();
        }

        Set<R> result = new HashSet<>();
        iterable.forEach(t -> result.add(mapper.apply(t)));

        return result;
    }
}