package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.model.User;
import com.ulger.sk.usermanager.api.user.model.UserImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SimpleUserCacheTest {

    private SimpleUserCache cache;

    private User user1 = UserImp.newInstance(1, "1@gmail.com", "fn1", "ln1");
    private User user2 = UserImp.newInstance(2, "2@gmail.com", "fn2", "ln2");
    private User user3 = UserImp.newInstance(3, "3@gmail.com", "fn3", "ln3");
    private User user4 = UserImp.newInstance(4, "4@gmail.com", "fn4", "ln4");
    private User user1Copy = UserImp.newInstance(1, "user1Copy@gmail.com", "fn1User1Copy", "ln1User1Copy");
    private User user2Copy = UserImp.newInstance(2, "user2Copy@gmail.com", "fn2User2Copy", "ln2User2Copy");
    private User user3Copy = UserImp.newInstance(3, "user3Copy@gmail.com", "fn3User3Copy", "ln3User3Copy");
    private User user4Copy = UserImp.newInstance(4, "user4Copy@gmail.com", "fn4User4Copy", "ln4User4Copy");

    @BeforeEach
    void setUp() {
        cache = new SimpleUserCache();

        assertTrue(cache.getAll().isEmpty());
        assertNull(cache.get(user1.getId()));
        assertNull(cache.get(user2.getId()));
        assertNull(cache.get(user3.getId()));
        assertNull(cache.get(user4.getId()));
        assertNull(cache.get(user1Copy.getId()));
    }

    @Test
    void test_empty_cache() {
        assertTrue(cache.getAll().isEmpty());
    }

    @Test
    void test_adding() {
        cache.add(user1);
        assertEquals(user1, cache.get(user1.getId()));
        assertNull(cache.get(user2.getId()));
        assertEquals(1, cache.getAll().size());

        cache.add(user2);
        assertEquals(user2, cache.get(user2.getId()));
        assertEquals(2, cache.getAll().size());

        cache.add(Arrays.asList(user3, user4));
        assertEquals(user3, cache.get(user3.getId()));
        assertEquals(user4, cache.get(user4.getId()));
        assertEquals(4, cache.getAll().size());
    }

    @Test
    void test_overriding() {
        cache.add(user1);
        assertEquals(user1, cache.get(user1.getId()));

        cache.add(user1Copy);
        assertNotEquals(user1.getEmail(), cache.get(user1.getId()).getEmail());
        assertNotEquals(user1.getFirstName(), cache.get(user1.getId()).getFirstName());
        assertNotEquals(user1.getLastName(), cache.get(user1.getId()).getLastName());

        cache.add(user1Copy);
        assertEquals(user1Copy.getEmail(), cache.get(user1.getId()).getEmail());
        assertEquals(user1Copy.getFirstName(), cache.get(user1.getId()).getFirstName());
        assertEquals(user1Copy.getLastName(), cache.get(user1.getId()).getLastName());

        cache.add(Arrays.asList(user2, user3));
        cache.add(Arrays.asList(user2Copy, user3Copy));

        assertNotEquals(user2.getEmail(), cache.get(user2.getId()).getEmail());
        assertNotEquals(user2.getFirstName(), cache.get(user2.getId()).getFirstName());
        assertNotEquals(user2.getLastName(), cache.get(user2.getId()).getLastName());
        assertEquals(user2Copy.getEmail(), cache.get(user2.getId()).getEmail());
        assertEquals(user2Copy.getFirstName(), cache.get(user2.getId()).getFirstName());
        assertEquals(user2Copy.getLastName(), cache.get(user2.getId()).getLastName());

        assertNotEquals(user3.getEmail(), cache.get(user3.getId()).getEmail());
        assertNotEquals(user3.getFirstName(), cache.get(user3.getId()).getFirstName());
        assertNotEquals(user3.getLastName(), cache.get(user3.getId()).getLastName());
        assertEquals(user3Copy.getEmail(), cache.get(user3.getId()).getEmail());
        assertEquals(user3Copy.getFirstName(), cache.get(user3.getId()).getFirstName());
        assertEquals(user3Copy.getLastName(), cache.get(user3.getId()).getLastName());
    }

    @Test
    void adding_multiple_values_fail_case() {
        cache.add(user1);
        cache.add(user2);
        cache.add(user3);
        cache.add(user4);
        assertEquals(4, cache.getAll().size());

        user3Copy = Mockito.mock(User.class);
        when(user3Copy.getId()).thenReturn(null);

        try {
            cache.add(Arrays.asList(user1Copy, user2Copy, user3Copy, user4Copy));
        } catch (Exception e) {
            assertNotEquals(user1.getEmail(), cache.get(user1.getId()).getEmail());
            assertNotEquals(user2.getEmail(), cache.get(user2.getId()).getEmail());
            assertNotEquals(user3.getEmail(), cache.get(user3.getId()).getEmail());
            assertNotEquals(user4.getEmail(), cache.get(user4.getId()).getEmail());
        }
    }

    @Test
    void adding_single_value_fail_case() {
        cache.add(user1);

        user2 = Mockito.mock(User.class);
        when(user2.getId()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> cache.add(user2));
        assertNull(cache.get(user2.getId()));
    }
}