package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.*;
import com.ulger.sk.usermanager.exception.ApiException;
import com.ulger.sk.usermanager.exception.DataAccessException;
import com.ulger.sk.usermanager.exception.TestReasonException;
import com.ulger.sk.usermanager.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationMode;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DefaultUserManagerTest {

    final MutableUserModificationData data1 = getModificationData("email1@gmail.com", "fn1", "ln1", "hpw1");
    final MutableUserModificationData data2 = getModificationData("email2@gmail.com", "fn2", "ln2", "hpw2");
    final MutableUserModificationData data3 = getModificationData("email3@gmail.com", "fn3", "ln3", "hpw3");
    final MutableUserModificationData data4 = getModificationData("email4@gmail.com", "fn4", "ln4", "hpw4");
    final MutableUserModificationData data5 = getModificationData("email5@gmail.com", "fn5", "ln5", "hpw5");

    private UserDaoMock userDao;
    private PasswordEncoder passwordEncoder;
    private PasswordPolicyManager passwordPolicyManager;
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        this.userDao = new UserDaoMock();
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
        assertThrows(IllegalArgumentException.class, () -> userManager.updateUser(null));
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

        assertNotNull(user.getId());
        assertTrue(hasSameData(user, data1));
        assertTrue(hasSameData(userManager.getUserByEmail(data1.getEmail()), data1));

        data1.setId(user.getId());
        data1.setEmail("email");
        data1.setFirstName("firstname");
        data1.setLastName("lastname");
        data1.setHashPassword("hashpassword");

        User updatedUser = userManager.updateUser(data1);

        assertTrue(hasSameData(updatedUser, data1));
        assertTrue(hasSameData(updatedUser, userManager.getUserByEmail(data1.getEmail())));
    }

    @Test
    void test_update_illegal_attempting() {
        String email = data1.getEmail();
        User user = userManager.createUser(data1);

        data1.setId(null);
        data1.setEmail(null);
        assertThrows(IllegalArgumentException.class, () -> userManager.updateUser(data1));
        assertThrows(IllegalArgumentException.class, () -> userManager.changePassword(data1));

        data1.setEmail(email);
        data1.setFirstName("Ahmet");
        assertEquals("Ahmet", userManager.updateUser(data1).getFirstName());

        data1.setEmail("unknown");
        data1.setFirstName("Ahmet2");
        assertThrows(UserNotFoundException.class, () -> userManager.updateUser(data1));
        assertThrows(UserNotFoundException.class, () -> userManager.changePassword(data1));

        data1.setEmail(email);
        data1.setId(user.getId());
        data1.setFirstName("Ahmet3");
        assertEquals("Ahmet3", userManager.updateUser(data1).getFirstName());

        data1.setEmail("changing");
        data1.setId(user.getId());
        data1.setFirstName("Ahmet4");
        assertThrows(ApiException.class, () -> userManager.updateUser(data1));
        assertThrows(ApiException.class, () -> userManager.changePassword(data1));
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

        data2.setId(user2.getId());
        data3.setId(data2.getId());
        data3.setEmail(data1.getEmail());

        DataAccessException exception = assertThrows(DataAccessException.class, () -> userManager.updateUser(data3));
        assertEquals(TestReasonException.Reason.UNIQUE_FIELD, ((TestReasonException) exception.getCause()).getReason());

        data4.setId(data2.getId());
        data4.setEmail(data1.getEmail());

        DataAccessException exception2 = assertThrows(DataAccessException.class, () -> userManager.updateUser(data4));
        assertEquals(TestReasonException.Reason.UNIQUE_FIELD, ((TestReasonException) exception2.getCause()).getReason());

        data5.setId(data2.getId());
        data5.setFirstName(data1.getFirstName());
        data5.setLastName(data1.getLastName());
        data5.setHashPassword(data1.getHashPassword());

        User updatedUser5 = userManager.updateUser(data5);
        assertEquals(2, userDao.find().size());
        assertEquals(updatedUser5.getId(), data2.getId());
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
        userManager.changePassword(data1);
        assertEquals("hashed2", userDao.findByEmail(data1.getEmail()).getCredential());
    }

    @Test
    void test_events() throws InterruptedException {
        UserModificationEventListener listener = Mockito.mock(UserModificationEventListener.class);
        UserModificationEventListener listener2 = Mockito.mock(UserModificationEventListener.class);
        ((DefaultUserManager) userManager).addEventListener(listener);
        ((DefaultUserManager) userManager).addEventListener(listener2);

        when(listener.isAsync()).thenReturn(false);
        when(listener2.isAsync()).thenReturn(true);

        User user1 = userManager.createUser(data1);
        verify(listener, times(1)).onModified(anyObject());
        verify(listener, new VerificationMode() {
            @Override
            public void verify(VerificationData verificationData) {
                UserModificationEvent event = (UserModificationEvent) verificationData.getAllInvocations().get(1).getRawArguments()[0];
                assertEquals(UserModificationEvent.EventType.CREATE, event.getEventType());
                assertEquals(data1, event.getModificationData());
                assertEquals(data1.getEmail(), event.getModifiedData().getEmail());
            }
        }).onModified(anyObject());

        data1.setId(user1.getId());
        data1.setFirstName("Ahmet3");

        userManager.updateUser(data1);
        verify(listener, new VerificationMode() {
            @Override
            public void verify(VerificationData verificationData) {
                UserModificationEvent event = (UserModificationEvent) verificationData.getAllInvocations().get(3).getRawArguments()[0];
                assertEquals(UserModificationEvent.EventType.UPDATE, event.getEventType());
                assertEquals(data1, event.getModificationData());
                assertEquals(data1.getFirstName(), event.getModifiedData().getFirstName());
            }
        }).onModified(anyObject());

        verify(listener2, times(2)).onModified(anyObject());
    }

    private static MutableUserModificationData getModificationData(String email, String firstName, String lastName, String password) {
        MutableUserModificationData request = new MutableUserModificationData();

        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setRawPassword(password);

        return request;
    }

    private static boolean hasSameData(User user, MutableUserModificationData data) {
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

        return Objects.equals(user.getId(), user2.getId())
                && Objects.equals(user.getEmail(), user2.getEmail())
                && Objects.equals(user.getDisplayName(), user2.getDisplayName());
    }
}