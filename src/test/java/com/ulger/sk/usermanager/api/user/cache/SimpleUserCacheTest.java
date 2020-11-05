package com.ulger.sk.usermanager.api.user.cache;

import com.ulger.sk.usermanager.api.user.core.DefaultUser;
import com.ulger.sk.usermanager.api.user.core.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SimpleUserCacheTest {

    private SimpleUserCache cache;

    private User user1 = DefaultUser.newInstance("1", "1@gmail.com", "fn1", "ln1", "cr1");
    private User user2 = DefaultUser.newInstance("2", "2@gmail.com", "fn2", "ln2", "cr2");
    private User user3 = DefaultUser.newInstance("3", "3@gmail.com", "fn3", "ln3", "cr3");
    private User user4 = DefaultUser.newInstance("4", "4@gmail.com", "fn4", "ln4", "cr4");
    private User user1Copy = DefaultUser.newInstance("1", "1@gmail.com", "fn1User1Copy", "ln1User1Copy", "cr1");
    private User user2Copy = DefaultUser.newInstance("2", "2@gmail.com", "fn2User2Copy", "ln2User2Copy", "cr2");
    private User user3Copy = DefaultUser.newInstance("3", "3@gmail.com", "fn3User3Copy", "ln3User3Copy", "cr3");
    private User user4Copy = DefaultUser.newInstance("4", "4@gmail.com", "fn4User4Copy", "ln4User4Copy", "cr4");

    @BeforeEach
    void setUp() {
        cache = new SimpleUserCache();

        assertTrue(cache.getAll().isEmpty());
        assertNull(cache.get(user1.getEmail()));
        assertNull(cache.get(user2.getEmail()));
        assertNull(cache.get(user3.getEmail()));
        assertNull(cache.get(user4.getEmail()));
        assertNull(cache.get(user1Copy.getEmail()));
    }

    @Test
    void test_empty_cache() {
        assertTrue(cache.getAll().isEmpty());
    }

    @Test
    void test_adding() {
        cache.add(user1);
        assertTrue(user1 == cache.get(user1.getEmail()));
        assertNull(cache.get(user2.getEmail()));
        assertEquals(1, cache.getAll().size());

        cache.add(user2);
        assertTrue(user2 == cache.get(user2.getEmail()));
        assertEquals(2, cache.getAll().size());

        cache.add(Arrays.asList(user3, user4));
        assertTrue(user3 == cache.get(user3.getEmail()));
        assertTrue(user4 == cache.get(user4.getEmail()));
        assertEquals(4, cache.getAll().size());
    }

    @Test
    void test_overriding() {
        cache.add(user1);
        assertTrue(user1 == cache.get(user1.getEmail()));

        cache.add(user1Copy);
        assertEquals(user1.getEmail(), cache.get(user1.getEmail()).getEmail());
        assertNotEquals(user1.getFirstName(), cache.get(user1.getEmail()).getFirstName());
        assertNotEquals(user1.getLastName(), cache.get(user1.getEmail()).getLastName());

        cache.add(user1Copy);
        assertEquals(user1Copy.getEmail(), cache.get(user1.getEmail()).getEmail());
        assertEquals(user1Copy.getFirstName(), cache.get(user1.getEmail()).getFirstName());
        assertEquals(user1Copy.getLastName(), cache.get(user1.getEmail()).getLastName());

        cache.add(Arrays.asList(user2, user3));
        cache.add(Arrays.asList(user2Copy, user3Copy));

        assertEquals(user2.getEmail(), cache.get(user2.getEmail()).getEmail());
        assertNotEquals(user2.getFirstName(), cache.get(user2.getEmail()).getFirstName());
        assertNotEquals(user2.getLastName(), cache.get(user2.getEmail()).getLastName());
        assertEquals(user2Copy.getEmail(), cache.get(user2.getEmail()).getEmail());
        assertEquals(user2Copy.getFirstName(), cache.get(user2.getEmail()).getFirstName());
        assertEquals(user2Copy.getLastName(), cache.get(user2.getEmail()).getLastName());

        assertEquals(user3.getEmail(), cache.get(user3.getEmail()).getEmail());
        assertNotEquals(user3.getFirstName(), cache.get(user3.getEmail()).getFirstName());
        assertNotEquals(user3.getLastName(), cache.get(user3.getEmail()).getLastName());
        assertEquals(user3Copy.getEmail(), cache.get(user3.getEmail()).getEmail());
        assertEquals(user3Copy.getFirstName(), cache.get(user3.getEmail()).getFirstName());
        assertEquals(user3Copy.getLastName(), cache.get(user3.getEmail()).getLastName());
    }

    @Test
    void adding_multiple_values_fail_case() {
        cache.add(user1);
        cache.add(user2);
        cache.add(user3);
        cache.add(user4);
        assertEquals(4, cache.getAll().size());

        user3Copy = Mockito.mock(User.class);
        when(user3Copy.getEmail()).thenReturn(null);

        try {
            cache.add(Arrays.asList(user1Copy, user2Copy, user3Copy, user4Copy));
        } catch (Exception e) {
            assertNotEquals(user1.getEmail(), cache.get(user1.getEmail()).getEmail());
            assertNotEquals(user2.getEmail(), cache.get(user2.getEmail()).getEmail());
            assertNotEquals(user3.getEmail(), cache.get(user3.getEmail()).getEmail());
            assertNotEquals(user4.getEmail(), cache.get(user4.getEmail()).getEmail());
        }
    }

    @Test
    void adding_single_value_fail_case() {
        user1 = null;
        assertThrows(IllegalArgumentException.class, () -> cache.add(user1));
        assertNull(cache.get(user2.getEmail()));
    }
}