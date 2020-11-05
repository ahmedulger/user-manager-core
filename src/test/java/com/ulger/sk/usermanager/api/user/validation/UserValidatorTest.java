package com.ulger.sk.usermanager.api.user.validation;

import com.ulger.sk.usermanager.api.user.core.MutableUserAdapter;
import com.ulger.sk.usermanager.api.user.password.DefaultPasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.password.PasswordPolicyManager;
import org.apache.commons.validator.routines.EmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        MutableUserAdapter mutableUserAdapter = createSimpleData();
        assertTrue(validationStrategy.validate(mutableUserAdapter).isValid());

        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, " ", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, null, MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, "abc", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, "abc@", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, "abc@com", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, "abc@.com", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, "1@.com", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, "1@1", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, "@1.com", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, "@1.com.tr", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);

        setAndTestAndRollback(validationStrategy, mutableUserAdapter, true, "abc@a.com", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, true, "a-a@a.com", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, true, "a-a@a.com.tr", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, true, "a-@a.com", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, true, "a-@a.com.tr", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, true, "-@a.com", MutableUserAdapter::getEmail, mutableUserAdapter::setEmail);
    }

    @Test
    void test_password_validation() {
        MutableUserAdapter mutableUserAdapter = createSimpleData();
        assertTrue(validationStrategy.validate(mutableUserAdapter).isValid());

        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, " ", MutableUserAdapter::getRawPassword, mutableUserAdapter::setRawPassword);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, null, MutableUserAdapter::getRawPassword, mutableUserAdapter::setRawPassword);
    }

    @Test
    void test_username_validation() {
        MutableUserAdapter mutableUserAdapter = createSimpleData();
        assertTrue(validationStrategy.validate(mutableUserAdapter).isValid());

        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, " ", MutableUserAdapter::getFirstName, mutableUserAdapter::setFirstName);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, null, MutableUserAdapter::getFirstName, mutableUserAdapter::setFirstName);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, " ", MutableUserAdapter::getLastName, mutableUserAdapter::setLastName);
        setAndTestAndRollback(validationStrategy, mutableUserAdapter, false, null, MutableUserAdapter::getLastName, mutableUserAdapter::setLastName);
    }

    private void setAndTestAndRollback(
            UserValidator strategy,
            MutableUserAdapter source,
            boolean result,
            String newValue,
            Function<MutableUserAdapter, String> fieldGetter,
            Consumer<String> fieldSetter) {

        String originalValue = fieldGetter.apply(source);
        fieldSetter.accept(newValue);
        assertEquals(result, strategy.validate(source).isValid());
        fieldSetter.accept(originalValue);
    }

    private MutableUserAdapter createSimpleData() {
        MutableUserAdapter request = new MutableUserAdapter();

        request.setEmail("abc@gmail.com");
        request.setFirstName("Ahmet");
        request.setLastName("Ülger");
        request.setRawPassword("123");

        return request;
    }
}