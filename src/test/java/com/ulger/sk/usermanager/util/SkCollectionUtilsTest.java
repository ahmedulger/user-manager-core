package com.ulger.sk.usermanager.util;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SkCollectionUtilsTest {

    Function<String, Integer> numericMapper = (text) -> Integer.valueOf(text);
    Iterable<String> numericList = Stream.of("1", "2", "3", "3", "4", "4").collect(Collectors.toList());

    @Test
    void test_any_null_input() {
        assertThrows(NullPointerException.class, () -> SkCollectionUtils.convertAndCreateSet(null, numericMapper));
        assertThrows(NullPointerException.class, () -> SkCollectionUtils.convertAndCreateSet(numericList, null));
        assertThrows(NullPointerException.class, () -> SkCollectionUtils.convertAndCreateSet(null, null));
    }

    @Test
    void test_empty_iterable() {
        assertTrue(SkCollectionUtils.convertAndCreateSet(Lists.newArrayList(), numericMapper).isEmpty());
    }

    @Test
    void test_multiple_elements() {
        assertEquals(4, SkCollectionUtils.convertAndCreateSet(numericList, numericMapper).size());
    }
}