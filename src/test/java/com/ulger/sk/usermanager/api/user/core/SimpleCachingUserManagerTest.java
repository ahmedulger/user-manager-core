package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.core.password.DefaultPasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.core.password.PasswordEncoder;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class SimpleCachingUserManagerTest {

    final MutableUserAdapter data1 = getModificationData("email1@gmail.com", "fn1", "ln1", "hpw1");

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private PasswordPolicyManager passwordPolicyManager;
    private UserManager defaultUserManager;
    private SimpleCachingUserManager cachingUserManager;

    @BeforeEach
    void setUp() {
        this.userDao = new UserDaoH2();
        this.passwordEncoder = Mockito.mock(PasswordEncoder.class);
        this.passwordPolicyManager = new DefaultPasswordPolicyManager();
        this.defaultUserManager = new DefaultUserManager(passwordEncoder, UserValidationContextInitializer.getDefault(passwordPolicyManager), userDao);
        this.cachingUserManager = new SimpleCachingUserManager(defaultUserManager);
    }

    @Test
    void test_create_none_existing_user() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        User user1 = cachingUserManager.createUser(data1);

        assertFalse(user1 == defaultUserManager.getUserByEmail(user1.getEmail()));
        assertTrue(user1 == cachingUserManager.getUserByEmail(user1.getEmail()));
    }

    @Test
    void test_update_check_cache() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        User user1 = cachingUserManager.createUser(data1);
        assertFalse(user1 == defaultUserManager.getUserByEmail(user1.getEmail()));
        assertTrue(user1 == cachingUserManager.getUserByEmail(user1.getEmail()));

        when(passwordEncoder.encode(anyString())).thenReturn(null);
        User updatedUser = cachingUserManager.updateUser(data1);
        assertFalse(user1 == updatedUser);
        assertFalse(updatedUser == defaultUserManager.getUserByEmail(user1.getEmail()));
        assertTrue(updatedUser == cachingUserManager.getUserByEmail(user1.getEmail()));
    }

    private static MutableUserAdapter getModificationData(String email, String firstName, String lastName, String password) {
        MutableUserAdapter request = new MutableUserAdapter();

        request.setUsername(email.substring(0, email.indexOf("@")));
        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setRawPassword(password);
        request.setHashPassword(password);

        return request;
    }
}