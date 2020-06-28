package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class DefaultPasswordPolicyManager implements PasswordPolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPasswordPolicyManager.class);

    private PasswordPolicyConfiguration policyConfiguration;
    private Collection<PasswordPolicyCondition> additionalConditions;
    private I18NHelper i18NHelper;

    public DefaultPasswordPolicyManager() {
        init();
    }

    public DefaultPasswordPolicyManager(PasswordPolicyConfiguration policyConfiguration) {
        this.policyConfiguration = policyConfiguration;
        init();
    }

    public DefaultPasswordPolicyManager(PasswordPolicyConfiguration policyConfiguration, I18NHelper i18NHelper) {
        this(policyConfiguration);
        this.i18NHelper = i18NHelper;
        init();
    }

    public DefaultPasswordPolicyManager(Collection<PasswordPolicyCondition> additionalConditions) {
        this.additionalConditions = additionalConditions;
        init();
    }

    public DefaultPasswordPolicyManager(Collection<PasswordPolicyCondition> additionalConditions, I18NHelper i18NHelper) {
        this(additionalConditions);
        this.i18NHelper = i18NHelper;
        init();
    }

    public DefaultPasswordPolicyManager(PasswordPolicyConfiguration policyConfiguration, Collection<PasswordPolicyCondition> additionalConditions) {
        this.policyConfiguration = policyConfiguration;
        this.additionalConditions = additionalConditions;
        init();
    }

    public DefaultPasswordPolicyManager(PasswordPolicyConfiguration policyConfiguration, Collection<PasswordPolicyCondition> additionalConditions, I18NHelper i18NHelper) {
        this(policyConfiguration, additionalConditions);
        this.i18NHelper = i18NHelper;
        init();
    }

    private final void init() {
        logger.info("[PasswordPolicyManagerImpl] Context initialized :: policyConfiguration={}, additionalConditions={}", policyConfiguration, additionalConditions);

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
            result.addError(i18NHelper.getMessage("validation.password.too.short", policyConfiguration.getMinimumLength()));
            return;
        }

        if (password.length() > policyConfiguration.getMaximumLength()) {
            result.addError(i18NHelper.getMessage("validation.password.too.long", policyConfiguration.getMaximumLength()));
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