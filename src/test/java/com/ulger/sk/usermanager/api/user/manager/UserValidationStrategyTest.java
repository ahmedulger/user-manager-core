package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.DefaultPasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.PasswordPolicyManager;
import org.apache.commons.validator.routines.EmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationStrategyTest {

    private EmailValidator emailValidator;
    private PasswordPolicyManager passwordPolicyManager;
    private UserValidationStrategy validationStrategy;

    @BeforeEach
    void setUp() {
        this.emailValidator = EmailValidator.getInstance();
        this.passwordPolicyManager = new DefaultPasswordPolicyManager();
        this.validationStrategy = new MockUserValidationStrategy(emailValidator, passwordPolicyManager);
    }

    @Test
    void test_email_validation() {
        MutableUserModificationData mutableUserModificationData = createSimpleData();
        assertTrue(validationStrategy.validate(mutableUserModificationData).isValid());

        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, " ", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, null, MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, "abc", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, "abc@", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, "abc@com", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, "abc@.com", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, "1@.com", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, "1@1", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, "@1.com", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, "@1.com.tr", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);

        setAndTestAndRollback(validationStrategy, mutableUserModificationData, true, "abc@a.com", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, true, "a-a@a.com", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, true, "a-a@a.com.tr", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, true, "a-@a.com", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, true, "a-@a.com.tr", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, true, "-@a.com", MutableUserModificationData::getEmail, mutableUserModificationData::setEmail);
    }

    @Test
    void test_password_validation() {
        MutableUserModificationData mutableUserModificationData = createSimpleData();
        assertTrue(validationStrategy.validate(mutableUserModificationData).isValid());

        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, " ", MutableUserModificationData::getRawPassword, mutableUserModificationData::setRawPassword);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, null, MutableUserModificationData::getRawPassword, mutableUserModificationData::setRawPassword);
    }

    @Test
    void test_username_validation() {
        MutableUserModificationData mutableUserModificationData = createSimpleData();
        assertTrue(validationStrategy.validate(mutableUserModificationData).isValid());

        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, " ", MutableUserModificationData::getFirstName, mutableUserModificationData::setFirstName);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, null, MutableUserModificationData::getFirstName, mutableUserModificationData::setFirstName);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, " ", MutableUserModificationData::getLastName, mutableUserModificationData::setLastName);
        setAndTestAndRollback(validationStrategy, mutableUserModificationData, false, null, MutableUserModificationData::getLastName, mutableUserModificationData::setLastName);
    }

    private void setAndTestAndRollback(
            UserValidationStrategy strategy,
            MutableUserModificationData source,
            boolean result,
            String newValue,
            Function<MutableUserModificationData, String> fieldGetter,
            Consumer<String> fieldSetter) {

        String originalValue = fieldGetter.apply(source);
        fieldSetter.accept(newValue);
        assertEquals(result, strategy.validate(source).isValid());
        fieldSetter.accept(originalValue);
    }

    private MutableUserModificationData createSimpleData() {
        MutableUserModificationData request = new MutableUserModificationData();

        request.setEmail("abc@gmail.com");
        request.setFirstName("Ahmet");
        request.setLastName("Ülger");
        request.setRawPassword("123");

        return request;
    }
}