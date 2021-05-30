package com.ulger.sk.usermanager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;

class SkAssertionsTest {

    @Test
    void test_null_check() {
        assertThrows(IllegalArgumentException.class, () -> SkAssertions.notNull(null));
        assertThrows(IllegalArgumentException.class, () -> SkAssertions.notNull(null, null));
        SkAssertions.notNull(new Object());
        SkAssertions.notNull(new Object(), anyString());
    }

    @Test
    void test_blank_check() {
        assertThrows(IllegalArgumentException.class, () -> SkAssertions.notBlank(anyString(), null));
        assertThrows(IllegalArgumentException.class, () -> SkAssertions.notBlank(anyString(), " "));
        SkAssertions.notBlank(anyString(), "a ");
    }
}