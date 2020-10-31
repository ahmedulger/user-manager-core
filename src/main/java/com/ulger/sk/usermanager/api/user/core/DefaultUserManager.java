package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.*;
import com.ulger.sk.usermanager.api.user.password.PasswordEncoder;
import com.ulger.sk.usermanager.api.user.password.PasswordPolicyManager;
import com.ulger.sk.usermanager.exception.IllegalParameterException;
import com.ulger.sk.usermanager.localization.DefaultI18NHelper;
import com.ulger.sk.usermanager.localization.I18NHelper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.ulger.sk.usermanager.SkAssertions.notBlank;
import static com.ulger.sk.usermanager.SkAssertions.notNull;

/**
 * Simple implementation of {@link UserManager}
 */
public class DefaultUserManager implements UserManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserManager.class);

    private DefaultUserValidationContext defaultUserValidationContext;
    private PasswordEncoder passwordEncoder;
    private UserDao userDao;
    private I18NHelper i18NHelper;
    private Collection<UserModificationEventListener> modificationEventListeners;

    /**
     * Constructs instance with UserDao.
     * If no event listener given than any events will be published after user modification
     * @param userDao
     */
    public DefaultUserManager(PasswordEncoder passwordEncoder, PasswordPolicyManager passwordPolicyManager, UserDao userDao) {
        this.defaultUserValidationContext = new DefaultUserValidationContext(passwordPolicyManager);
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

    /**
     * Constructs instance with UserDao.
     * If no event listener given than any events will be published after user modification
     * @param userDao
     * @param modificationEventListeners
     */
    public DefaultUserManager(PasswordEncoder passwordEncoder, PasswordPolicyManager passwordPolicyManager, UserDao userDao, Collection<UserModificationEventListener> modificationEventListeners, I18NHelper i18nHelper) {
        this(passwordEncoder, passwordPolicyManager, userDao, i18nHelper);
        this.modificationEventListeners = modificationEventListeners;
        init();
    }

    private final void init() {
        if (passwordEncoder == null) {
            logger.error("[init] No password encoder found, PasswordEncoder is required");
            throw new IllegalArgumentException("No password encoder found, PasswordEncoder is required");
        }

        if (this.modificationEventListeners == null) {
            logger.warn("[init] No modification listener found");
            this.modificationEventListeners = new LinkedList<>();
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
        notNull(modificationData, "User creation data should not be null");

        MutableUserModificationData mutableData = new MutableUserModificationData(modificationData);
        validate(mutableData, DefaultUserValidationContext.OPERATION_CREATE);

        encryptPassword(mutableData);

        return createOrUpdateUser(mutableData);
    }

    @Override
    public User updateUser(Object id, UserModificationData modificationData) {
        logger.info("[updateUser] User is updating :: id={}, data={}", id, modificationData);
        notNull(id);
        notNull(modificationData);

        MutableUserModificationData mutableData = new MutableUserModificationData(modificationData);
        validate(mutableData, DefaultUserValidationContext.OPERATION_UPDATE);

        User user = getUserById(modificationData);
        if (user == null) {
            logger.error("[updateUser] User not found :: id={}, email={}", id, modificationData.getEmail());
            throw new UserNotFoundException(i18NHelper.getMessage("operation.user.not.found", id, modificationData.getEmail()));
        }

        loadId(mutableData, user.getUsername());
        user = createOrUpdateUser(mutableData);
        logger.info("[updateUser] User has been updated :: email={}", user.getEmail());

        return user;
    }

    @Override
    public User changePassword(Object id, UserModificationData modificationData) {
        logger.info("[changePassword] User's password is updating :: data={}", modificationData);
        notNull(modificationData);

        if (Objects.isNull(id)) {
            throw new IllegalParameterException(UserField.USERNAME.getName(), "Id should be given");
        }

        MutableUserModificationData mutableData = new MutableUserModificationData(modificationData);
        validate(mutableData, DefaultUserValidationContext.OPERATION_CHANGE_PASSWORD);

        User user = getUserById(id);
        if (user == null) {
            throw new UserNotFoundException(i18NHelper.getMessage("operation.user.not.found", modificationData.getEmail(), id));
        }

        if (passwordEncoder.matches(modificationData.getRawPassword(), user.getCredential())) {
            throw new IllegalParameterException(UserField.PASSWORD.getName(), i18NHelper.getMessage("operation.password.change.same"));
        }

        encryptPassword(mutableData);
        loadId(mutableData, user.getUsername());

        user = createOrUpdateUser(mutableData);
        logger.info("[changePassword] User's password has been changed successfully :: email={}", user.getEmail());
        return user;
    }

    /**
     * Add event listener that is going to be triggered after user modified
     * @param userModificationEventListener
     */
    public void addEventListener(UserModificationEventListener userModificationEventListener) {
        this.modificationEventListeners.add(userModificationEventListener);
        logger.info("[addEventListener] New event listener has been added");
    }

    private User getUserById(Object id) {
        return userDao.findByUsername(id);
    }

    private void loadId(MutableUserModificationData data, Object id) {
        data.setId(id);
    }

    private void encryptPassword(MutableUserModificationData mutableUserModificationData) {
        mutableUserModificationData.setHashPassword(encryptPassword(mutableUserModificationData.getRawPassword()));
    }

    private String encryptPassword(String rawPassword) {
        notBlank(UserField.PASSWORD.getName(), rawPassword);
        return passwordEncoder.encode(rawPassword);
    }

    private void validate(UserModificationData userModificationData, int operation) {
        UserValidationResult validationResult = defaultUserValidationContext.validate(userModificationData, operation);

        if (!validationResult.isValid()) {
            logger.info("[validate] User is not valid :: validationResult={}", validationResult);
            throw new ValidationException(validationResult.getErrorCollection());
        }
    }

    private User createOrUpdateUser(MutableUserModificationData modificationData) {
        logger.info("[createOrUpdateUser] User is saving :: data={}", modificationData);

        User user = userDao.save(modificationData);
        logger.info("[createOrUpdateUser] User has been saved");

        triggerEvents(modificationData, user);

        return user;
    }

    private void triggerEvents(UserModificationData sourceData, User user) {
        if (modificationEventListeners.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[triggerEvents] No listener found to trigger");
            }

            return;
        }

        UserModificationEvent.EventType eventType = UserModificationEvent.EventType.UPDATE;
        if (Objects.isNull(sourceData.getId())) {
            eventType = UserModificationEvent.EventType.CREATE;
        }

        UserModificationEvent event = new UserModificationEvent(eventType, sourceData, user, LocalDateTime.now());

        if (logger.isDebugEnabled()) {
            logger.debug("[triggerEvents] Triggering events with data :: event={}", event);
        }

        for (UserModificationEventListener listener : modificationEventListeners) {
            if (listener.isAsync()) {
                CompletableFuture.runAsync(() -> listener.onModified(event));
                continue;
            }

            listener.onModified(event);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("defaultUserValidationContext", defaultUserValidationContext)
                .append("passwordEncoder", passwordEncoder)
                .append("userDao", userDao)
                .append("modificationEventListeners", modificationEventListeners)
                .toString();
    }
}