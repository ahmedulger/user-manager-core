package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.MutableUserAdapter;
import com.ulger.sk.usermanager.api.user.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.password.SimplePasswordCheckingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class UserValidationContextTest {

    private PasswordPolicyManager passwordPolicyManager;
    private UserValidationContext validationContext;

    @BeforeEach
    void setUp() {
        this.passwordPolicyManager = Mockito.mock(PasswordPolicyManager.class);
        this.validationContext = new UserValidationContext(passwordPolicyManager);
    }

    @Test
    void test_invalid_operation_id() {
        MutableUserAdapter data = createSimpleData();
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(data, 4).isValid());
    }

    @Test
    void test_invalid_strategy() {
        MutableUserAdapter data = createSimpleData();
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(data, (UserValidator) null).isValid());
    }

    @Test
    void test_invalid_input() {
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(null, UserValidationContext.OPERATION_CREATE).isValid());
    }

    @Test
    void test_valid_operation_id() {
        MutableUserAdapter data = createSimpleData();

        when(passwordPolicyManager.checkPolicy(anyString())).thenReturn(new SimplePasswordCheckingResult());

        assertTrue(validationContext.validate(data, UserValidationContext.OPERATION_CREATE).isValid());
        assertTrue(validationContext.validate(data, UserValidationContext.OPERATION_UPDATE).isValid());
    }

    private MutableUserAdapter createSimpleData() {
        MutableUserAdapter data = new MutableUserAdapter();

        data.setEmail("abc@gmail.com");
        data.setFirstName("Ahmet");
        data.setLastName("Ülger");
        data.setRawPassword("123");

        return data;
    }
}