package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.DefaultUserValidationContext;
import com.ulger.sk.usermanager.api.user.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.SimplePasswordCheckingResult;
import com.ulger.sk.usermanager.api.user.UserValidationStrategy;
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
        MutableUserModificationData data = createSimleData();
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(data, 4).isValid());
    }

    @Test
    void test_invalid_strategy() {
        MutableUserModificationData data = createSimleData();
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(data, (UserValidationStrategy) null).isValid());
    }

    @Test
    void test_invalid_input() {
        assertThrows(IllegalArgumentException.class, () -> validationContext.validate(null, DefaultUserValidationContext.OPERATION_CREATE).isValid());
    }

    @Test
    void test_valid_operation_id() {
        MutableUserModificationData data = createSimleData();

        when(passwordPolicyManager.checkPolicy(anyString())).thenReturn(new SimplePasswordCheckingResult());

        assertTrue(validationContext.validate(data, DefaultUserValidationContext.OPERATION_CREATE).isValid());
        assertTrue(validationContext.validate(data, DefaultUserValidationContext.OPERATION_UPDATE).isValid());
    }

    private MutableUserModificationData createSimleData() {
        MutableUserModificationData data = new MutableUserModificationData();

        data.setEmail("abc@gmail.com");
        data.setFirstName("Ahmet");
        data.setLastName("Ülger");
        data.setRawPassword("123");
        data.setConfirmPassword("123");

        return data;
    }
}