package com.ulger.sk.usermanager.api.user.password;

import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class DefaultPasswordPolicyManager implements PasswordPolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPasswordPolicyManager.class);

    private Collection<PasswordPolicyCondition> policyConditions;
    private I18NHelper i18NHelper;

    public DefaultPasswordPolicyManager() {
        init();
    }

    public DefaultPasswordPolicyManager(Collection<PasswordPolicyCondition> policyConditions) {
        this.policyConditions = policyConditions;
        init();
    }

    public DefaultPasswordPolicyManager(Collection<PasswordPolicyCondition> policyConditions, I18NHelper i18NHelper) {
        this(policyConditions);
        this.i18NHelper = i18NHelper;
        init();
    }

    private final void init() {
        logger.info("[PasswordPolicyManagerImpl] Context initialized :: policyConditions={}", policyConditions);

        if (this.i18NHelper == null) {
            logger.warn("[init] I18NHelper implementation not found, initializing with DefaultI18NHelper");
            this.i18NHelper = new DefaultI18NHelper();
        }
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