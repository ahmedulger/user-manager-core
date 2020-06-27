package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.DefaultPasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.PasswordEncoder;
import com.ulger.sk.usermanager.api.user.PasswordPolicyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleCachingUserManagerTest {

    final MutableUserModificationData data1 = getModificationData("email1@gmail.com", "fn1", "ln1", "hpw1");
    final MutableUserModificationData data2 = getModificationData("email2@gmail.com", "fn2", "ln2", "hpw2");
    final MutableUserModificationData data3 = getModificationData("email3@gmail.com", "fn3", "ln3", "hpw3");
    final MutableUserModificationData data4 = getModificationData("email4@gmail.com", "fn4", "ln4", "hpw4");
    final MutableUserModificationData data5 = getModificationData("email5@gmail.com", "fn5", "ln5", "hpw5");

    private UserDaoMock userDao;
    private PasswordEncoder passwordEncoder;
    private PasswordPolicyManager passwordPolicyManager;
    private UserManager defaultUserManager;
    private SimpleCachingUserManager cachingUserManager;

    @BeforeEach
    void setUp() {
        this.userDao = new UserDaoMock();
        this.passwordEncoder = Mockito.mock(PasswordEncoder.class);
        this.passwordPolicyManager = new DefaultPasswordPolicyManager();
        this.defaultUserManager = new DefaultUserManager(passwordEncoder, passwordPolicyManager, userDao);
        this.cachingUserManager = new SimpleCachingUserManager(defaultUserManager);
    }

    @Test
    void test_create_none_existing_user() {
        User user1 = cachingUserManager.createUser(data1);

        assertFalse(user1 == defaultUserManager.getUserByEmail(user1.getEmail()));
        assertTrue(user1 == cachingUserManager.getUserByEmail(user1.getEmail()));
    }

    @Test
    void test_update_check_cache() {
        User user1 = cachingUserManager.createUser(data1);
        assertFalse(user1 == defaultUserManager.getUserByEmail(user1.getEmail()));
        assertTrue(user1 == cachingUserManager.getUserByEmail(user1.getEmail()));

        User updatedUser = cachingUserManager.updateUser(data1);
        assertFalse(user1 == updatedUser);
        assertFalse(updatedUser == defaultUserManager.getUserByEmail(user1.getEmail()));
        assertTrue(updatedUser == cachingUserManager.getUserByEmail(user1.getEmail()));
    }

    private static MutableUserModificationData getModificationData(String email, String firstName, String lastName, String password) {
        MutableUserModificationData request = new MutableUserModificationData();

        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setRawPassword(password);
        request.setConfirmPassword(password);

        return request;
    }
}