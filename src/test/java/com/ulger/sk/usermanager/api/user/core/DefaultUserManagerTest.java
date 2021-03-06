package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.core.password.DefaultPasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.core.password.MockPasswordEncoder;
import com.ulger.sk.usermanager.api.user.core.password.PasswordEncoder;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.validation.ValidationException;
import com.ulger.sk.usermanager.exception.DataAccessException;
import com.ulger.sk.usermanager.exception.IllegalParameterException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class DefaultUserManagerTest {

    final MutableUserAdapter data1 = getModificationData("email1@gmail.com", "fn1", "ln1", "hpw12345");
    final MutableUserAdapter data2 = getModificationData("email2@gmail.com", "fn2", "ln2", "hpw22345");
    final MutableUserAdapter data3 = getModificationData("email3@gmail.com", "fn3", "ln3", "hpw32345");
    final MutableUserAdapter data4 = getModificationData("email4@gmail.com", "fn4", "ln4", "hpw42345");
    final MutableUserAdapter data5 = getModificationData("email5@gmail.com", "fn5", "ln5", "hpw52345");

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private PasswordPolicyManager passwordPolicyManager;
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        this.userDao = new UserDaoH2();
        this.passwordEncoder = new MockPasswordEncoder("hashed");
        this.passwordPolicyManager = new DefaultPasswordPolicyManager();
        this.userManager = new DefaultUserManager(passwordEncoder, UserValidationContextInitializer.getDefault(passwordPolicyManager), userDao);
    }

    @Test
    void test_get_user_by_email_blank_input() {
        assertThrows(IllegalArgumentException.class, () -> userManager.getUserByEmail(null));
        assertThrows(IllegalArgumentException.class, () -> userManager.getUserByEmail(" "));
    }

    @Test
    void test_null_data() {
        assertThrows(UserOperationException.class, () -> userManager.createUser(null));
    }

    @Test
    void test_create_blank_data_fields() {
        data1.setEmail(" ");
        data2.setFirstName(" ");
        data3.setLastName(" ");
        data4.setRawPassword(" ");

        assert ValidationException.class == assertThrows(UserOperationException.class, () -> userManager.createUser(data1)).getCause().getClass();
        assert ValidationException.class == assertThrows(UserOperationException.class, () -> userManager.createUser(data1)).getCause().getClass();
        assert ValidationException.class == assertThrows(UserOperationException.class, () -> userManager.createUser(data1)).getCause().getClass();
        assert ValidationException.class == assertThrows(UserOperationException.class, () -> userManager.createUser(data1)).getCause().getClass();
    }

    @Test
    void test_create_null_data_fields() {
        data1.setEmail(null);
        data2.setFirstName(null);
        data3.setLastName(null);
        data4.setRawPassword(null);

        assert ValidationException.class == assertThrows(UserOperationException.class, () -> userManager.createUser(data1)).getCause().getClass();
        assert ValidationException.class == assertThrows(UserOperationException.class, () -> userManager.createUser(data1)).getCause().getClass();
        assert ValidationException.class == assertThrows(UserOperationException.class, () -> userManager.createUser(data1)).getCause().getClass();
        assert ValidationException.class == assertThrows(UserOperationException.class, () -> userManager.createUser(data1)).getCause().getClass();
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
        data1.setEmail("email@gmail.com");
        data1.setFirstName("firstname");
        data1.setLastName("lastname");
        data1.setHashPassword("hashpassword");

        User updatedUser = userManager.updateUser(data1);

        assertTrue(hasSameData(updatedUser, data1));
        assertTrue(hasSameData(updatedUser, userManager.getUserByEmail(data1.getEmail())));
    }

    @Test
    void test_update_illegal_attempting() {
        String username = data1.getUsername();
        String email = data1.getEmail();
        User user = userManager.createUser(data1);

        data1.setUsername(null);
        data1.setEmail(null);
        Exception exception = assertThrows(UserOperationException.class, () -> userManager.updateUser(data1));
        assertEquals(IllegalArgumentException.class, exception.getCause().getClass());

        exception = assertThrows(UserOperationException.class, () -> userManager.changePassword(data1.getEmail(), "hpw12345", data1.getCredential()));
        assertEquals(IllegalParameterException.class, exception.getCause().getClass());

        data1.setUsername(username);
        data1.setEmail(email);
        data1.setFirstName("Ahmet");
        assertEquals("Ahmet", userManager.updateUser(data1).getFirstName());

        data1.setUsername("unknown");
        data1.setFirstName("Ahmet2");
        exception = assertThrows(UserOperationException.class, () -> userManager.updateUser(data1));
        assertEquals(DataAccessException.class, exception.getCause().getClass());

        exception = assertThrows(UserOperationException.class, () -> userManager.changePassword(data1.getEmail(), "hpw12345", data1.getCredential()));
        assertEquals(ValidationException.class, exception.getCause().getClass());

        data1.setEmail(email);
        data1.setUsername(user.getUsername());
        data1.setFirstName("Ahmet3");
        assertEquals("Ahmet3", userManager.updateUser(data1).getFirstName());

        data1.setEmail("changing");
        data1.setUsername(user.getUsername());
        data1.setFirstName("Ahmet4");
        assertThrows(UserOperationException.class, () -> userManager.updateUser(data1));
        assertThrows(UserOperationException.class, () -> userManager.changePassword(data1.getEmail(), "hpw1", data1.getCredential()));
    }

    @Test
    void test_create_existing_user_unique_fields() {
        User user1 = userManager.createUser(data1);
        User user2 = userManager.createUser(data2);

        assertEquals(2, userDao.find().size());

        data3.setEmail(data2.getEmail());

        UserOperationException exception = assertThrows(UserOperationException.class, () -> userManager.createUser(data3));
        assertEquals(JdbcSQLIntegrityConstraintViolationException.class, exception.getCause().getCause().getClass());

        data4.setEmail(data2.getEmail());

        UserOperationException exception2 = assertThrows(UserOperationException.class, () -> userManager.createUser(data4));
        assertEquals(JdbcSQLIntegrityConstraintViolationException.class, exception.getCause().getCause().getClass());

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

        UserOperationException exception = assertThrows(UserOperationException.class, () -> userManager.updateUser(data3));
        assertTrue(exception.getCause() instanceof DataAccessException);
        assertEquals(JdbcSQLIntegrityConstraintViolationException.class, exception.getCause().getCause().getClass());

        data4.setUsername(data2.getUsername());
        data4.setEmail(data1.getEmail());

        UserOperationException exception2 = assertThrows(UserOperationException.class, () -> userManager.updateUser(data4));
        assertTrue(exception.getCause() instanceof DataAccessException);
        assertEquals(JdbcSQLIntegrityConstraintViolationException.class, exception.getCause().getCause().getClass());

        data5.setUsername(data2.getUsername());
        data5.setFirstName(data1.getFirstName());
        data5.setLastName(data1.getLastName());
        data5.setHashPassword(data1.getHashPassword());

        User updatedUser5 = userManager.updateUser(data5);
        assertEquals(2, userDao.find().size());
        assertEquals(updatedUser5.getUsername(), data2.getUsername());
        assertEquals(updatedUser5.getDisplayName(), data1.getFirstName() + " " + data1.getLastName());
    }

    @Test
    void test_encrypt_password() {
        userManager.createUser(data1);

        assertEquals("hashed" + data1.getRawPassword(), userDao.findByEmail(data1.getEmail()).getCredential());
    }

    @Test
    void test_change_password() {
        userManager.createUser(data1);
        assertEquals("hashed" + data1.getRawPassword(), userDao.findByEmail(data1.getEmail()).getCredential());

        userManager.changePassword(data1.getEmail(), "hpw12345", "xx");
        assertEquals("hashedxx", userDao.findByEmail(data1.getEmail()).getCredential());
    }

    private static MutableUserAdapter getModificationData(String email, String firstName, String lastName, String password) {
        MutableUserAdapter request = new MutableUserAdapter();

        request.setUsername(email.substring(0, email.indexOf("@")));
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