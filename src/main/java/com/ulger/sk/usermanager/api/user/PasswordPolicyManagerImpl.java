package com.ulger.sk.usermanager.api.user;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class PasswordPolicyManagerImpl implements PasswordPolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(PasswordPolicyManagerImpl.class);

    private PasswordPolicyConfiguration policyConfiguration;
    private Collection<PasswordPolicyCondition> additionalConditions;

    public PasswordPolicyManagerImpl() {
        init();
    }

    public PasswordPolicyManagerImpl(PasswordPolicyConfiguration policyConfiguration) {
        this.policyConfiguration = policyConfiguration;
        init();
    }

    public PasswordPolicyManagerImpl(Collection<PasswordPolicyCondition> additionalConditions) {
        this.additionalConditions = additionalConditions;
        init();
    }

    public PasswordPolicyManagerImpl(PasswordPolicyConfiguration policyConfiguration, Collection<PasswordPolicyCondition> additionalConditions) {
        this.policyConfiguration = policyConfiguration;
        this.additionalConditions = additionalConditions;
        init();
    }

    private final void init() {
        logger.info("[PasswordPolicyManagerImpl] Context initialized :: policyConfiguration={}, additionalConditions={}", policyConfiguration, additionalConditions);
    }

    @Override
    public PasswordCheckingResult checkPolicy(String password) {
        if (logger.isDebugEnabled()) {
            logger.debug("[checkPolicy] Password checking started :: password={}", password);
        }

        PasswordCheckingResult result = new SimplePasswordCheckingResult();

        if (StringUtils.isBlank(password)) {
            result.addError("Password should be given");
            return result;
        }

        checkByConfiguration(password, result);
        checkByConditions(password, result);

        return result;
    }

    private void checkByConfiguration(String password, PasswordCheckingResult result) {
        if (policyConfiguration == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("[checkByConfiguration] No policy configuration found to check");
            }

            return;
        }

        if (password.length() < policyConfiguration.getMinimumLength()) {
            result.addError("Password is too short. Password should be at least " + policyConfiguration.getMinimumLength() + " digits");
            return;
        }

        if (password.length() > policyConfiguration.getMaximumLength()) {
            result.addError("Password is too long. Password can not be longer than " + policyConfiguration.getMaximumLength() + " digits");
        }
    }

    private void checkByConditions(String password, PasswordCheckingResult result) {
        if (CollectionUtils.isEmpty(additionalConditions)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[checkByConditions] No policy conditions found to check");
            }

            return;
        }

        for (PasswordPolicyCondition condition : additionalConditions) {
            PasswordCheckingResult conditionResult = condition.check(password);
            if (conditionResult != null && conditionResult.hasError()) {
                result.addError(conditionResult.getErrors());
            }
        }
    }
}