package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.MockUserModificationData;
import com.ulger.sk.usermanager.api.user.core.UserOperation;
import com.ulger.sk.usermanager.api.user.core.UserValidationContextInitializer;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.core.password.SimplePasswordCheckingResult;
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
        this.validationContext = UserValidationContextInitializer.getDefault(passwordPolicyManager);
    }

    @Test
    void test_invalid_operation_id() {
        MockUserModificationData data = createSimpleData();
        validationContext.setValidationStrategy(UserOperation.CREATE, null);
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(data, UserOperation.CREATE).isValid());
    }

    @Test
    void test_invalid_strategy() {
        MockUserModificationData data = createSimpleData();
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(data, (UserValidator) null).isValid());
    }

    @Test
    void test_invalid_input() {
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(null, UserOperation.CREATE).isValid());
    }

    @Test
    void test_valid_operation_id() {
        MockUserModificationData data = createSimpleData();

        when(passwordPolicyManager.checkPolicy(anyString())).thenReturn(new SimplePasswordCheckingResult());

        assertTrue(validationContext.validate(data, UserOperation.CREATE).isValid());
        assertTrue(validationContext.validate(data, UserOperation.UPDATE).isValid());
    }

    private MockUserModificationData createSimpleData() {
        MockUserModificationData data = new MockUserModificationData();

        data.setUsername("abc");
        data.setEmail("abc@gmail.com");
        data.setFirstName("Ahmet");
        data.setLastName("Ülger");
        data.setRawPassword("123");

        return data;
    }
}