package com.ulger.sk.usermanager.api.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordPolicyManagerTest {

    private static final int MIN_PASS_LENGTH = 3;
    private static final int MAX_PASS_LENGTH = 6;

    private PasswordPolicyManager policyManager;

    @BeforeEach
    void setUp() {
        this.policyManager = new PasswordPolicyManagerImpl();
    }

    @Test
    void test_blank_input() {
        assertTrue(policyManager.checkPolicy(null).hasError());
        assertTrue(policyManager.checkPolicy(" ").hasError());
    }

    @Test
    void test_none_configuration_none_conditions() {
        assertFalse(policyManager.checkPolicy("a").hasError());
    }

    @Test
    void test_configuration() {
        PasswordPolicyConfiguration configuration = new PasswordPolicyConfiguration(MIN_PASS_LENGTH, MAX_PASS_LENGTH);
        this.policyManager = new PasswordPolicyManagerImpl(configuration);

        PasswordCheckingResult result1 = policyManager.checkPolicy("a1");
        PasswordCheckingResult result2 = policyManager.checkPolicy("a123456");
        PasswordCheckingResult result3 = policyManager.checkPolicy("a12345");

        assertTrue(result1.hasError());
        assertEquals(1, result1.getErrors().size());
        assertTrue(result2.hasError());
        assertEquals(1, result2.getErrors().size());
        assertFalse(result3.hasError());
        assertEquals(0, result3.getErrors().size());

        assertEquals("Password is too short. Password should be at least " + MIN_PASS_LENGTH + " digits", result1.getErrors().iterator().next());
        assertEquals("Password is too long. Password can not be longer than " + MAX_PASS_LENGTH + " digits", result2.getErrors().iterator().next());
    }

    @Test
    void test_conditions() {
        List<PasswordPolicyCondition> conditions = new ArrayList<>();
        conditions.add(new PasswordPolicyConditionMock((pwd, result) -> {
            if (pwd.contains(".")) {result.addError("conditionError1");}
        }));

        conditions.add(new PasswordPolicyConditionMock((pwd, result) -> {
            if (pwd.contains("_")) {result.addError("conditionError2");}
        }));

        conditions.add(new PasswordPolicyConditionMock((pwd, result) -> {
            if (pwd.contains("+")) {result.addError("conditionError3");}
        }));

        this.policyManager = new PasswordPolicyManagerImpl(conditions);

        PasswordCheckingResult result1 = policyManager.checkPolicy("a.a_+s");
        PasswordCheckingResult result2 = policyManager.checkPolicy("a.a+");

        assertEquals(3, result1.getErrors().size());
        assertEquals(2, result2.getErrors().size());

        List<String> errors1 = result1.getErrors().stream().collect(Collectors.toList());
        List<String> errors2 = result2.getErrors().stream().collect(Collectors.toList());

        assertEquals("conditionError1", errors1.get(0));
        assertEquals("conditionError2", errors1.get(1));
        assertEquals("conditionError3", errors1.get(2));

        assertEquals("conditionError1", errors2.get(0));
        assertEquals("conditionError3", errors2.get(1));

    }
}