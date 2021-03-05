package com.ulger.sk.usermanager.api.user.core.password;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

public class DefaultPasswordPolicyManager implements PasswordPolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPasswordPolicyManager.class);

    private Collection<PasswordPolicyCondition> policyConditions;

    public DefaultPasswordPolicyManager() {
        init();
    }

    public DefaultPasswordPolicyManager(PasswordPolicyCondition policyCondition) {
        this.policyConditions = Arrays.asList(policyCondition);
        init();
    }

    public DefaultPasswordPolicyManager(Collection<PasswordPolicyCondition> policyConditions) {
        this.policyConditions = policyConditions;
        init();
    }

    private void init() {
        logger.info("[PasswordPolicyManagerImpl] Context initialized :: policyConditions={}", policyConditions);
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

        checkByConditions(password, result);

        return result;
    }

    private void checkByConditions(String password, PasswordCheckingResult result) {
        if (CollectionUtils.isEmpty(policyConditions)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[checkByConditions] No policy conditions found to check");
            }

            return;
        }

        for (PasswordPolicyCondition condition : policyConditions) {
            PasswordCheckingResult conditionResult = condition.check(password);
            if (conditionResult != null && conditionResult.hasError()) {
                result.addError(conditionResult.getErrors());
            }
        }
    }
}