package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.MockUserModificationData;
import com.ulger.sk.usermanager.api.user.core.password.DefaultPasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.core.password.PasswordPolicyManager;
import org.apache.commons.validator.routines.EmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidatorTest {

    private EmailValidator emailValidator;
    private PasswordPolicyManager passwordPolicyManager;
    private UserValidator validationStrategy;

    @BeforeEach
    void setUp() {
        this.emailValidator = EmailValidator.getInstance();
        this.passwordPolicyManager = new DefaultPasswordPolicyManager();
        this.validationStrategy = new MockUserValidator(emailValidator, passwordPolicyManager);
    }

    @Test
    void test_email_validation() {
        MockUserModificationData data = createSimpleData();
        assertTrue(validationStrategy.validate(data).isValid());

        setAndTestAndRollback(validationStrategy, data, false, " ", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, false, null, MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, false, "abc", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, false, "abc@", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, false, "abc@com", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, false, "abc@.com", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, false, "1@.com", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, false, "1@1", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, false, "@1.com", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, false, "@1.com.tr", MockUserModificationData::getEmail, data::setEmail);

        setAndTestAndRollback(validationStrategy, data, true, "abc@a.com", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, true, "a-a@a.com", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, true, "a-a@a.com.tr", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, true, "a-@a.com", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, true, "a-@a.com.tr", MockUserModificationData::getEmail, data::setEmail);
        setAndTestAndRollback(validationStrategy, data, true, "-@a.com", MockUserModificationData::getEmail, data::setEmail);
    }

    @Test
    void test_password_validation() {
        MockUserModificationData modificationData = createSimpleData();
        assertTrue(validationStrategy.validate(modificationData).isValid());

        setAndTestAndRollback(validationStrategy, modificationData, false, " ", MockUserModificationData::getRawPassword, modificationData::setRawPassword);
        setAndTestAndRollback(validationStrategy, modificationData, false, null, MockUserModificationData::getRawPassword, modificationData::setRawPassword);
    }

    @Test
    void test_username_validation() {
        MockUserModificationData modificationData = createSimpleData();
        assertTrue(validationStrategy.validate(modificationData).isValid());

        setAndTestAndRollback(validationStrategy, modificationData, false, " ", MockUserModificationData::getFirstName, modificationData::setFirstName);
        setAndTestAndRollback(validationStrategy, modificationData, false, null, MockUserModificationData::getFirstName, modificationData::setFirstName);
        setAndTestAndRollback(validationStrategy, modificationData, false, " ", MockUserModificationData::getLastName, modificationData::setLastName);
        setAndTestAndRollback(validationStrategy, modificationData, false, null, MockUserModificationData::getLastName, modificationData::setLastName);
    }

    private void setAndTestAndRollback(
            UserValidator strategy,
            MockUserModificationData source,
            boolean result,
            String newValue,
            Function<MockUserModificationData, String> fieldGetter,
            Consumer<String> fieldSetter) {

        String originalValue = fieldGetter.apply(source);
        fieldSetter.accept(newValue);
        assertEquals(result, strategy.validate(source).isValid());
        fieldSetter.accept(originalValue);
    }

    private MockUserModificationData createSimpleData() {
        MockUserModificationData modificationData = new MockUserModificationData();

        modificationData.setUsername("abc");
        modificationData.setEmail("abc@gmail.com");
        modificationData.setFirstName("Ahmet");
        modificationData.setLastName("Ülger");
        modificationData.setRawPassword("123");

        return modificationData;
    }
}