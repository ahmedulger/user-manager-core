package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.core.password.DefaultPasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.core.password.PasswordEncoder;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.validation.ValidationException;
import com.ulger.sk.usermanager.exception.DataAccessException;
import com.ulger.sk.usermanager.exception.TestReasonException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class DefaultUserManagerTest {

    final MutableUserAdapter data1 = getModificationData("email1@gmail.com", "fn1", "ln1", "hpw1");
    final MutableUserAdapter data2 = getModificationData("email2@gmail.com", "fn2", "ln2", "hpw2");
    final MutableUserAdapter data3 = getModificationData("email3@gmail.com", "fn3", "ln3", "hpw3");
    final MutableUserAdapter data4 = getModificationData("email4@gmail.com", "fn4", "ln4", "hpw4");
    final MutableUserAdapter data5 = getModificationData("email5@gmail.com", "fn5", "ln5", "hpw5");

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private PasswordPolicyManager passwordPolicyManager;
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        this.userDao = new UserDaoH2();
        this.passwordEncoder = Mockito.mock(PasswordEncoder.class);
        this.passwordPolicyManager = new DefaultPasswordPolicyManager();
        this.userManager = new DefaultUserManager(passwordEncoder, passwordPolicyManager, userDao);
    }

    @Test
    void test_get_user_by_email_blank_input() {
        assertThrows(IllegalArgumentException.class, () -> userManager.getUserByEmail(null));
        assertThrows(IllegalArgumentException.class, () -> userManager.getUserByEmail(" "));
    }

    @Test
    void test_null_data() {
        assertThrows(IllegalArgumentException.class, () -> userManager.createUser(null));
        assertThrows(IllegalArgumentException.class, () -> userManager.updateUser("", null));
        assertThrows(IllegalArgumentException.class, () -> userManager.updateUser(" ", null));
        assertThrows(IllegalArgumentException.class, () -> userManager.updateUser(null,null));
    }

    @Test
    void test_create_blank_data_fields() {
        data1.setEmail(" ");
        data2.setFirstName(" ");
        data3.setLastName(" ");
        data4.setRawPassword(" ");

        assertThrows(ValidationException.class, () -> userManager.createUser(data1));
        assertThrows(ValidationException.class, () -> userManager.createUser(data2));
        assertThrows(ValidationException.class, () -> userManager.createUser(data3));
        assertThrows(ValidationException.class, () -> userManager.createUser(data4));
    }

    @Test
    void test_create_null_data_fields() {
        data1.setEmail(null);
        data2.setFirstName(null);
        data3.setLastName(null);
        data4.setRawPassword(null);

        assertThrows(ValidationException.class, () -> userManager.createUser(data1));
        assertThrows(ValidationException.class, () -> userManager.createUser(data2));
        assertThrows(ValidationException.class, () -> userManager.createUser(data3));
        assertThrows(ValidationException.class, () -> userManager.createUser(data4));
    }

    @Test
    void test_create_none_existing_user() {
        User user1 = userManager.createUser(data1);
        User user2 = userManager.createUser(data2);
        User user3 = userManager.createUser(data3);
        User user4 = userManager.createUser(data4);

        assertEquals(4, userDao.find().size());

        assertTrue(hasSameData(user1, data1));
        assertTrue(hasSameData(user2, data2));
        assertTrue(hasSameData(user3, data3));
        assertTrue(hasSameData(user4, data4));

        assertTrue(hasSameData(userManager.getUserByEmail(user1.getEmail()), data1));
        assertTrue(hasSameData(userManager.getUserByEmail(user2.getEmail()), data2));
        assertTrue(hasSameData(userManager.getUserByEmail(user3.getEmail()), data3));
        assertTrue(hasSameData(userManager.getUserByEmail(user4.getEmail()), data4));

        assertFalse(user1 == userManager.getUserByEmail(user1.getEmail()));
        assertFalse(user2 == userManager.getUserByEmail(user2.getEmail()));
        assertFalse(user3 == userManager.getUserByEmail(user3.getEmail()));
        assertFalse(user4 == userManager.getUserByEmail(user4.getEmail()));
    }

    @Test
    void test_update_existing_user_none_unique_fields() {
        User user = userManager.createUser(data1);

        assertNotNull(user.getUsername());
        assertTrue(hasSameData(user, data1));
        assertTrue(hasSameData(userManager.getUserByEmail(data1.getEmail()), data1));

        data1.setUsername(user.getUsername());
        data1.setEmail("email");
        data1.setFirstName("firstname");
        data1.setLastName("lastname");
        data1.setHashPassword("hashpassword");

        User updatedUser = userManager.updateUser(user.getUsername(), data1);

        assertTrue(hasSameData(updatedUser, data1));
        assertTrue(hasSameData(updatedUser, userManager.getUserByEmail(data1.getEmail())));
    }

    @Test
    void test_update_illegal_attempting() {
        String email = data1.getEmail();
        User user = userManager.createUser(data1);

        data1.setUsername(null);
        data1.setEmail(null);
        assertThrows(IllegalArgumentException.class, () -> userManager.updateUser(data1.getUsername(), data1));
        assertThrows(IllegalArgumentException.class, () -> userManager.changePassword(data1.getUsername(), "hpw1", data1.getCredential()));

        data1.setEmail(email);
        data1.setFirstName("Ahmet");
        assertEquals("Ahmet", userManager.updateUser(data1.getUsername(), data1).getFirstName());

        data1.setEmail("unknown");
        data1.setFirstName("Ahmet2");
        assertThrows(UserNotFoundException.class, () -> userManager.updateUser(data1.getUsername(), data1));
        assertThrows(UserNotFoundException.class, () -> userManager.changePassword(data1.getUsername(), "hpw1", data1.getCredential()));

        data1.setEmail(email);
        data1.setUsername(user.getUsername());
        data1.setFirstName("Ahmet3");
        assertEquals("Ahmet3", userManager.updateUser(data1.getUsername(), data1).getFirstName());

        data1.setEmail("changing");
        data1.setUsername(user.getUsername());
        data1.setFirstName("Ahmet4");
        assertThrows(UserOperationException.class, () -> userManager.updateUser(data1.getUsername(), data1));
        assertThrows(UserOperationException.class, () -> userManager.changePassword(data1.getUsername(), "hpw1", data1.getCredential()));
    }

    @Test
    void test_create_existing_user_unique_fields() {
        User user1 = userManager.createUser(data1);
        User user2 = userManager.createUser(data2);

        assertEquals(2, userDao.find().size());

        data3.setEmail(data2.getEmail());

        DataAccessException exception = assertThrows(DataAccessException.class, () -> userManager.createUser(data3));
        assertEquals(TestReasonException.Reason.UNIQUE_FIELD, ((TestReasonException) exception.getCause()).getReason());

        data4.setEmail(data2.getEmail());

        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> userManager.createUser(data4));
        assertEquals(TestReasonException.Reason.UNIQUE_FIELD, ((TestReasonException) exception2.getCause()).getReason());

        data5.setFirstName(data2.getFirstName());
        data5.setLastName(data2.getLastName());
        data5.setHashPassword(data2.getHashPassword());

        User updatedUser4 = userManager.createUser(data5);
        assertEquals(3, userDao.find().size());
        assertEquals(updatedUser4.getDisplayName(), data2.getFirstName() + " " + data2.getLastName());
    }

    @Test
    void test_update_existing_user_unique_fields() {
        User user1 = userManager.createUser(data1);
        User user2 = userManager.createUser(data2);

        assertEquals(2, userDao.find().size());

        data2.setUsername(user2.getUsername());
        data3.setUsername(data2.getUsername());
        data3.setEmail(data1.getEmail());

        DataAccessException exception = assertThrows(DataAccessException.class, () -> userManager.updateUser(data3.getUsername(), data3));
        assertEquals(TestReasonException.Reason.UNIQUE_FIELD, ((TestReasonException) exception.getCause()).getReason());

        data4.setUsername(data2.getUsername());
        data4.setEmail(data1.getEmail());

        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> userManager.updateUser(data4.getUsername(), data4));
        assertEquals(TestReasonException.Reason.UNIQUE_FIELD, ((TestReasonException) exception2.getCause()).getReason());

        data5.setUsername(data2.getUsername());
        data5.setFirstName(data1.getFirstName());
        data5.setLastName(data1.getLastName());
        data5.setHashPassword(data1.getHashPassword());

        User updatedUser5 = userManager.updateUser(data5.getUsername(), data5);
        assertEquals(2, userDao.find().size());
        assertEquals(updatedUser5.getUsername(), data2.getUsername());
        assertEquals(updatedUser5.getDisplayName(), data1.getFirstName() + " " + data1.getLastName());
    }

    @Test
    void test_encrypt_password() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        userManager.createUser(data1);

        assertEquals("hashed", userDao.findByEmail(data1.getEmail()).getCredential());
    }

    @Test
    void test_change_password() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        userManager.createUser(data1);
        assertEquals("hashed", userDao.findByEmail(data1.getEmail()).getCredential());

        when(passwordEncoder.encode(anyString())).thenReturn("hashed2");
        userManager.changePassword(data1.getUsername(), "hpw1", data1.getCredential());
        assertEquals("hashed2", userDao.findByEmail(data1.getEmail()).getCredential());
    }

    private static MutableUserAdapter getModificationData(String email, String firstName, String lastName, String password) {
        MutableUserAdapter request = new MutableUserAdapter();

        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setRawPassword(password);

        return request;
    }

    private static boolean hasSameData(User user, MutableUserAdapter data) {
        if (user == null || data == null) {
            return false;
        }

        return Objects.equals(user.getEmail(), data.getEmail())
                && Objects.equals(user.getDisplayName(), data.getFirstName() + " " + data.getLastName());
    }

    private static boolean hasSameData(User user, User user2) {
        if (user == null || user2 == null) {
            return false;
        }

        return Objects.equals(user.getUsername(), user2.getUsername())
                && Objects.equals(user.getEmail(), user2.getEmail())
                && Objects.equals(user.getDisplayName(), user2.getDisplayName());
    }
}