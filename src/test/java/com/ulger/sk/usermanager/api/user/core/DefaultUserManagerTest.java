package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.core.password.PasswordEncoder;
import com.ulger.sk.usermanager.api.user.validation.UserValidationResult;
import com.ulger.sk.usermanager.api.user.validation.UserValidator;
import com.ulger.sk.usermanager.api.user.validation.UserValidatorPicker;
import com.ulger.sk.usermanager.api.user.validation.ValidationException;
import com.ulger.sk.usermanager.apiresult.ErrorBag;
import com.ulger.sk.usermanager.apiresult.SimpleErrorBag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class DefaultUserManagerTest {

    @Mock
    private UserValidatorPicker userValidatorPicker;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private DefaultUserManager userManager;

    @Test
    void test_get_user_by_email_blank_input() {
        assertThrows(IllegalArgumentException.class, () -> userManager.getUserByEmail(null));
        assertThrows(IllegalArgumentException.class, () -> userManager.getUserByEmail(" "));
    }

    @Test
    void test_get_user_by_email_null_data() {
        Mockito
                .when(userDao.findByEmail(any()))
                .thenReturn(null);

        assert null == userManager.getUserByEmail("emailX");
    }

    @Test
    void test_get_user_by_email_successfully() {
        MockUser userTobeReturned = new MockUser();
        userTobeReturned.setEmail("emailX");

        Mockito
                .when(userDao.findByEmail(any()))
                .thenReturn(Optional.of(userTobeReturned));

        assert userManager
                .getUserByEmail("emailX")
                .getEmail()
                .equals("emailX");
    }

    @Test
    void test_find_all_user() {
        MockUser userTobeReturned1 = new MockUser();
        userTobeReturned1.setEmail("email1X");
        
        MockUser userTobeReturned2 = new MockUser();
        userTobeReturned2.setEmail("email2X");
        
        Mockito
                .when(userDao.find())
                .thenReturn(Arrays.asList(userTobeReturned1, userTobeReturned2));
        
        assert userManager.getAllUsers().size() == 2;
        assert userManager.getAllUsers().get(0).getEmail().equals("email1X");
        assert userManager.getAllUsers().get(1).getEmail().equals("email2X");
    }

    @Test
    void test_create_user_null_validator_picker() {
        Mockito
                .when(userValidatorPicker.pick(UserOperation.CREATE))
                .thenReturn(null);

        assertThrows(
                NullPointerException.class,
                () -> userManager.createUser(null));
    }

    @Test
    void test_create_user_invalid_data() {
        UserValidator mockValidator = Mockito.mock(UserValidator.class);

        ErrorBag errorBag = new SimpleErrorBag();
        errorBag.addErrorMessage("errorMessage1X");

        UserValidationResult validationResult = new UserValidationResult(errorBag);

        Mockito
                .when(mockValidator.validate(any()))
                .thenReturn(validationResult);

        Mockito
                .when(userValidatorPicker.pick(UserOperation.CREATE))
                .thenReturn(mockValidator);

        ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userManager.createUser(null));

        assert validationException.getErrorBag().getErrorMessages().size() == 1;
    }

    @Test
    void test_create_user_successfully() {
        MockUser userTobeReturned = new MockUser();
        userTobeReturned.setEmail("emailX");

        Mockito
                .when(userDao.create(any()))
                .thenReturn(userTobeReturned);

        assert userManager
                .createUser(null)
                .getEmail()
                .equals("emailX");
    }

    @Test
    void test_update_user_null_validator_picker() {
        Mockito
                .when(userValidatorPicker.pick(UserOperation.UPDATE))
                .thenReturn(null);

        assertThrows(
                NullPointerException.class,
                () -> userManager.updateUser(null));
    }

    @Test
    void test_update_user_invalid_data() {
        UserValidator mockValidator = Mockito.mock(UserValidator.class);

        ErrorBag errorBag = new SimpleErrorBag();
        errorBag.addErrorMessage("errorMessage1X");

        UserValidationResult validationResult = new UserValidationResult(errorBag);

        Mockito
                .when(mockValidator.validate(any()))
                .thenReturn(validationResult);

        Mockito
                .when(userValidatorPicker.pick(UserOperation.UPDATE))
                .thenReturn(mockValidator);

        ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userManager.updateUser(null));

        assert validationException.getErrorBag().getErrorMessages().size() == 1;
    }

    @Test
    void test_update_user_successfully() {
        MockUser userTobeReturned = new MockUser();
        userTobeReturned.setEmail("emailX");

        Mockito
                .when(userDao.update(any()))
                .thenReturn(userTobeReturned);

        assert userManager
                .updateUser(null)
                .getEmail()
                .equals("emailX");
    }

    @Test
    void test_change_password_blank_email() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userManager.changePassword("", "", ""));
    }

    @Test
    void test_change_password_null_validator_picker() {
        Mockito
                .when(userValidatorPicker.pick(UserOperation.CHANGE_PASSWORD))
                .thenReturn(null);

        assertThrows(
                NullPointerException.class,
                () -> userManager.changePassword("emailX", "", ""));
    }

    @Test
    void test_change_password_invalid_data() {
        UserValidator mockValidator = Mockito.mock(UserValidator.class);

        ErrorBag errorBag = new SimpleErrorBag();
        errorBag.addErrorMessage("errorMessage1X");

        UserValidationResult validationResult = new UserValidationResult(errorBag);

        Mockito
                .when(mockValidator.validate(any()))
                .thenReturn(validationResult);

        Mockito
                .when(userValidatorPicker.pick(UserOperation.CHANGE_PASSWORD))
                .thenReturn(mockValidator);

        ValidationException validationException = assertThrows(
                ValidationException.class,
                () -> userManager.changePassword("emailX", "", ""));

        assert validationException.getErrorBag().getErrorMessages().size() == 1;
    }

    @Test
    void test_change_password_user_not_found() {
        UserValidator mockValidator = Mockito.mock(UserValidator.class);
        UserValidationResult validationResult = new UserValidationResult();

        Mockito
                .when(mockValidator.validate(any()))
                .thenReturn(validationResult);

        Mockito
                .when(userValidatorPicker.pick(UserOperation.CHANGE_PASSWORD))
                .thenReturn(mockValidator);

        Mockito
                .when(userDao.findByEmail(eq(("emailX"))))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userManager.changePassword("emailX", "", ""));
    }

    @Test
    void test_change_password_password_not_matches() {
        UserValidationResult validationResult = new UserValidationResult();

        MockUser userTobeReturned = new MockUser();
        userTobeReturned.setEmail("emailX");

        UserValidator mockValidator = Mockito.mock(UserValidator.class);

        Mockito
                .when(mockValidator.validate(any()))
                .thenReturn(validationResult);

        Mockito
                .when(userValidatorPicker.pick(UserOperation.CHANGE_PASSWORD))
                .thenReturn(mockValidator);

        Mockito
                .when(userDao.findByEmail(eq(("emailX"))))
                .thenReturn(Optional.of(userTobeReturned));

        assertThrows(
                IllegalArgumentException.class,
                () -> userManager.changePassword("emailX", "", ""));
    }


    @Test
    void test_change_password_successfully() {
        UserValidationResult validationResult = new UserValidationResult();

        MockUser userTobeReturned = new MockUser();
        userTobeReturned.setEmail("emailX");
        userTobeReturned.setUsername("usernameX");

        UserValidator mockValidator = Mockito.mock(UserValidator.class);

        Mockito
                .when(mockValidator.validate(any()))
                .thenReturn(validationResult);

        Mockito
                .when(userValidatorPicker.pick(UserOperation.CHANGE_PASSWORD))
                .thenReturn(mockValidator);

        Mockito
                .when(userDao.findByEmail(eq(("emailX"))))
                .thenReturn(Optional.of(userTobeReturned));

        Mockito
                .when(passwordEncoder.encode(any()))
                .thenReturn("encodedX");

        Mockito
                .verify(userDao, times(1))
                .updatePasswordByUsername(eq("usernameX"), "encodedX");
    }
}