package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.validation.DefaultUserValidationContext;
import com.ulger.sk.usermanager.api.user.validation.UserValidator;
import com.ulger.sk.usermanager.api.user.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.password.SimplePasswordCheckingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class DefaultUserValidationContextTest {

    private PasswordPolicyManager passwordPolicyManager;
    private DefaultUserValidationContext validationContext;

    @BeforeEach
    void setUp() {
        this.passwordPolicyManager = Mockito.mock(PasswordPolicyManager.class);
        this.validationContext = new DefaultUserValidationContext(passwordPolicyManager);
    }

    @Test
    void test_invalid_operation_id() {
        MutableUserModificationData data = createSimpleData();
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(data, 4).isValid());
    }

    @Test
    void test_invalid_strategy() {
        MutableUserModificationData data = createSimpleData();
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(data, (UserValidator) null).isValid());
    }

    @Test
    void test_invalid_input() {
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(null, DefaultUserValidationContext.OPERATION_CREATE).isValid());
    }

    @Test
    void test_valid_operation_id() {
        MutableUserModificationData data = createSimpleData();

        when(passwordPolicyManager.checkPolicy(anyString())).thenReturn(new SimplePasswordCheckingResult());

        assertTrue(validationContext.validate(data, DefaultUserValidationContext.OPERATION_CREATE).isValid());
        assertTrue(validationContext.validate(data, DefaultUserValidationContext.OPERATION_UPDATE).isValid());
    }

    private MutableUserModificationData createSimpleData() {
        MutableUserModificationData data = new MutableUserModificationData();

        data.setEmail("abc@gmail.com");
        data.setFirstName("Ahmet");
        data.setLastName("Ülger");
        data.setRawPassword("123");

        return data;
    }
}