package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.password.PasswordEncoder;
import com.ulger.sk.usermanager.api.user.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.api.user.validation.UserValidationContext;
import com.ulger.sk.usermanager.api.user.validation.UserValidationResult;
import com.ulger.sk.usermanager.api.user.validation.ValidationException;
import com.ulger.sk.usermanager.exception.IllegalParameterException;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static com.ulger.sk.usermanager.SkAssertions.notBlank;

/**
 * Simple implementation of {@link UserManager}
 */
public class DefaultUserManager implements UserManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserManager.class);

    private UserValidationContext userValidationContext;
    private PasswordEncoder passwordEncoder;
    private UserDao userDao;
    private I18NHelper i18NHelper;

    /**
     * Constructs instance with UserDao.
     * If no event listener given than any events will be published after user modification
     * @param userDao
     */
    public DefaultUserManager(PasswordEncoder passwordEncoder, PasswordPolicyManager passwordPolicyManager, UserDao userDao) {
        this.userValidationContext = new UserValidationContext(passwordPolicyManager);
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
        init();
    }

    /**
     * Constructs instance with UserDao.
     * If no event listener given than any events will be published after user modification
     * @param userDao
     */
    public DefaultUserManager(PasswordEncoder passwordEncoder, PasswordPolicyManager passwordPolicyManager, UserDao userDao, I18NHelper i18nHelper) {
        this(passwordEncoder, passwordPolicyManager, userDao);
        this.i18NHelper = i18nHelper;
        init();
    }

    private final void init() {
        if (passwordEncoder == null) {
            logger.error("[init] No password encoder found, PasswordEncoder is required");
            throw new IllegalArgumentException("No password encoder found, PasswordEncoder is required");
        }

        if (this.i18NHelper == null) {
            logger.warn("[init] I18NHelper implementation not found, initializing with DefaultI18NHelper");
            this.i18NHelper = new DefaultI18NHelper();
        }
    }

    /**
     * Returns a {@link User} instance with given email. If no user found than returns null.
     * @param email the email of User. If no email given than throws {@link IllegalArgumentException}
     * @return User instance with given email
     */
    @Override
    public User getUserByEmail(String email) {
        if (logger.isDebugEnabled()) {
            logger.debug("[getUserByEmail] Getting user with email :: email={}", email);
        }

        notBlank(UserField.EMAIL.getName(), email);

        User user = userDao.findByEmail(email);

        if (user != null) {
            return user;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[getUserByEmail] No user found with email :: email={}", email);
        }

        return null;
    }

    /**
     * @return All users. If no user found than return empty list
     */
    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = userDao.find();

        if (logger.isDebugEnabled()) {
            logger.debug("[getAllUsers] found user count :: count={}", allUsers.size());
        }

        return allUsers;
    }

    @Override
    public User createUser(UserModificationData modificationData) {
        logger.info("[createUser] User is creating :: data={}", modificationData);

        // Validate
        MutableUserAdapter mutableUserAdapter = new MutableUserAdapter(modificationData);
        validate(mutableUserAdapter, UserOperation.CREATE);

        // Update user's raw password to the hashed password
        encryptPassword(mutableUserAdapter);

        User user = userDao.create(mutableUserAdapter);
        logger.info("[createUser] User has been created");

        return user;
    }

    @Override
    public User updateUser(String username, UserModificationData modificationData) {
        logger.info("[updateUser] User is updating :: username={}, data={}", username, modificationData);
        notBlank(UserField.USERNAME.getName(), username);

        // Validate
        MutableUserAdapter mutableUserAdapter = new MutableUserAdapter(modificationData);
        validate(modificationData, UserOperation.UPDATE);

        User user = userDao.updateByUsername(username, mutableUserAdapter);
        logger.info("[updateUser] User has been updated :: username={}", username);

        return user;
    }

    @Override
    public User changePassword(String username, String oldPassword, String newPassword) {
        logger.info("[changePassword] User's password is updating :: username={}", username);

        if (Objects.isNull(username)) {
            throw new IllegalParameterException(UserField.USERNAME.getName(), "Id should be given");
        }

        MutableUserAdapter mutableUserAdapter = new MutableUserAdapter();
        mutableUserAdapter.setRawPassword(oldPassword);
        validate(mutableUserAdapter, UserOperation.CHANGE_PASSWORD);

        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(i18NHelper.getMessage("operation.user.not.found", username));
        }

        if (passwordEncoder.matches(oldPassword, user.getCredential())) {
            throw new IllegalParameterException(UserField.PASSWORD.getName(), i18NHelper.getMessage("operation.password.change.same"));
        }

        // Update user's raw password to the hashed password
        encryptPassword(mutableUserAdapter);

        user = userDao.updateByUsername(username, mutableUserAdapter);
        logger.info("[changePassword] User's password has been changed successfully :: email={}", user.getEmail());

        return user;
    }

    private void encryptPassword(MutableUserAdapter mutableUserAdapter) {
        logger.info("[encryptPassword] encrypting user password");
        mutableUserAdapter.setHashPassword(encryptRawPassword(mutableUserAdapter.getRawPassword()));
    }

    private String encryptRawPassword(String rawPassword) {
        notBlank(UserField.PASSWORD.getName(), rawPassword);
        return passwordEncoder.encode(rawPassword);
    }

    private void validate(UserModificationData userModificationData, UserOperation operation) {
        UserValidationResult validationResult = userValidationContext.validate(userModificationData, operation);

        if (!validationResult.isValid()) {
            logger.info("[validate] User is not valid :: validationResult={}", validationResult);
            throw new ValidationException(validationResult.getErrorCollection());
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userValidationContext", userValidationContext)
                .append("passwordEncoder", passwordEncoder)
                .append("userDao", userDao)
                .toString();
    }
}