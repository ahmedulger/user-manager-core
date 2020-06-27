package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.*;
import com.ulger.sk.usermanager.exception.ApiException;
import com.ulger.sk.usermanager.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;
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
    private UserEntityBuilder userEntityBuilder;
    private UserDao userDao;
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
     * Constructs instance with UserDao and {@link UserEntityBuilder} parameter.
     * @param userEntityBuilder is useful to save waste converting operation between type of {@link User}
     *                          {@link UserDao} uses a {@link User} instance to save data to the source.
     *                          If no userEntityBuilder given than this manager passes {@link UserImp} to {@link UserDao}.
     *                          But if you give a {@link UserEntityBuilder} that creates a {@link User} directly without
     *                          unnecessary converting operation.
     * @param userDao
     */
    public DefaultUserManager(PasswordEncoder passwordEncoder, PasswordPolicyManager passwordPolicyManager, UserEntityBuilder userEntityBuilder, UserDao userDao) {
        this.defaultUserValidationContext = new DefaultUserValidationContext(passwordPolicyManager);
        this.passwordEncoder = passwordEncoder;
        this.userEntityBuilder = userEntityBuilder;
        this.userDao = userDao;
        init();
    }

    /**
     * Constructs instance with UserDao.
     * If no event listener given than any events will be published after user modification
     * @param userDao
     * @param modificationEventListeners
     */
    public DefaultUserManager(PasswordEncoder passwordEncoder, PasswordPolicyManager passwordPolicyManager, UserDao userDao, Collection<UserModificationEventListener> modificationEventListeners) {
        this.defaultUserValidationContext = new DefaultUserValidationContext(passwordPolicyManager);
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
        this.modificationEventListeners = modificationEventListeners;
        init();
    }

    /**
     * Constructs instance with UserDao.
     * If no event listener given than any events will be published after user modification
     *
     * @param userEntityBuilder is useful to save waste converting operation between type of {@link User}
     *                        {@link UserDao} uses a {@link User} instance to save data to the source.
     *                        If no userEntityBuilder given than this manager passes {@link UserImp} to {@link UserDao}.
     *                        But if you give a {@link UserEntityBuilder} that creates a {@link User} directly without
     *                        unnecessary converting operation.
     * @param userDao
     * @param modificationEventListeners
     */
    public DefaultUserManager(PasswordEncoder passwordEncoder, PasswordPolicyManager passwordPolicyManager, UserEntityBuilder userEntityBuilder, UserDao userDao, Collection<UserModificationEventListener> modificationEventListeners) {
        this.defaultUserValidationContext = new DefaultUserValidationContext(passwordPolicyManager);
        this.passwordEncoder = passwordEncoder;
        this.userEntityBuilder = userEntityBuilder;
        this.userDao = userDao;
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

        notBlank(UserFields.EMAIL, email);

        User user = userDao.findByEmail(email);

        if (user == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("[getUserByEmail] No user found with email :: email={}", email);
            }

            return null;
        }

        return user;
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

       /* return allUsers
                .stream()
                .map((entity) -> convertEntity(entity))
                .collect(Collectors.toList());*/

        return allUsers;
    }

    @Override
    public User createUser(UserModificationData userModificationData) {
        logger.info("[createUser] User is creating :: data={}", userModificationData);
        notNull(userModificationData, "User creation data should not be null");

        MutableUserModificationData mutableUserModificationData = new MutableUserModificationData(userModificationData);
        validate(mutableUserModificationData, DefaultUserValidationContext.OPERATION_CREATE);

        encryptPassword(mutableUserModificationData);

        User user = createOrUpdateUser(mutableUserModificationData);
        logger.info("[createUser] User has been created");

        return user;
    }

    @Override
    public User updateUser(UserModificationData userModificationData) {
        logger.info("[updateUser] User is updating :: data={}", userModificationData);
        notNull(userModificationData, "User update data should not be null");

        MutableUserModificationData mutableUserModificationData = new MutableUserModificationData(userModificationData);
        validate(mutableUserModificationData, DefaultUserValidationContext.OPERATION_UPDATE);

        if (Objects.isNull(mutableUserModificationData.getId()) && StringUtils.isBlank(mutableUserModificationData.getEmail())) {
            throw new IllegalArgumentException("id or email should be given to update operation");
        }

        User user = getUserByIdOrEmail(userModificationData);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " +
                    mutableUserModificationData.getId() + " or email: " + mutableUserModificationData.getEmail());
        }

        checkIfEmailChanged(userModificationData, user);
        updateId(mutableUserModificationData, user.getId());

        user = createOrUpdateUser(mutableUserModificationData);
        logger.info("[updateUser] User has been updated :: email={}", user.getEmail());

        return user;
    }

    @Override
    public void changePassword(UserModificationData userModificationData) {
        logger.info("[changePassword] User's password is updating :: data={}", userModificationData);
        notNull(userModificationData, "User update data should not be null");

        MutableUserModificationData mutableUserModificationData = new MutableUserModificationData(userModificationData);
        validate(mutableUserModificationData, DefaultUserValidationContext.OPERATION_CHANGE_PASSWORD);

        if (Objects.isNull(mutableUserModificationData.getId()) && StringUtils.isBlank(mutableUserModificationData.getEmail())) {
            throw new IllegalArgumentException("id or email should be given to change password");
        }

        User user = getUserByIdOrEmail(userModificationData);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " +
                    mutableUserModificationData.getId() + " or email: " + mutableUserModificationData.getEmail());
        }

        checkIfEmailChanged(userModificationData, user);
        encryptPassword(mutableUserModificationData);
        updateId(mutableUserModificationData, user.getId());

        user = createOrUpdateUser(mutableUserModificationData);
        logger.info("[changePassword] User's password has been changed successfully :: email={}", user.getEmail());
    }

    /**
     * Add event listener that is going to be triggered after user modified
     * @param userModificationEventListener
     */
    public void addEventListener(UserModificationEventListener userModificationEventListener) {
        this.modificationEventListeners.add(userModificationEventListener);
        logger.info("[addEventListener] New event listener has been added");
    }

    private User getUserByIdOrEmail(UserModificationData userModificationData) {
        if (!Objects.isNull(userModificationData.getId())) {
            return userDao.findById(userModificationData.getId());
        }

        return userDao.findByEmail(userModificationData.getEmail());
    }

    private void checkIfEmailChanged(UserModificationData userModificationData, User user) {
        if (!Objects.isNull(userModificationData.getId()) &&
                !StringUtils.isBlank(userModificationData.getEmail()) &&
                !userModificationData.getEmail().equals(user.getEmail())) {

            throw new ApiException("Email is one of none-updatable fields");
        }
    }

    private void updateId(MutableUserModificationData data, Object id) {
        data.setId(id);
    }

    private void encryptPassword(MutableUserModificationData mutableUserModificationData) {
        mutableUserModificationData.setHashPassword(encryptPassword(mutableUserModificationData.getRawPassword()));
    }

    private String encryptPassword(String rawPassword) {
        notBlank(UserFields.PASSWORD, rawPassword);
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

        User user;

        if (userEntityBuilder == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("[createOrUpdateUser] No entity builder found, using default");
            }

            user = createInstanceByModificationData(modificationData);
        } else {
            user = userEntityBuilder.build(modificationData);
        }

        user = userDao.save(user);
        logger.info("[createOrUpdateUser] User has been saved");

        triggerEvents(modificationData, user);

        return user;
    }

    private UserImp createInstanceByModificationData(MutableUserModificationData userModificationData) {
        return UserImp.Builder.anUserImp()
                .withId(userModificationData.getId())
                .withEmail(userModificationData.getEmail())
                .withFirstName(userModificationData.getFirstName())
                .withLastName(userModificationData.getLastName())
                .withCredential(userModificationData.getCredential())
                .build();
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
                .append("userEntityBuilder", userEntityBuilder)
                .append("userDao", userDao)
                .append("modificationEventListeners", modificationEventListeners)
                .toString();
    }
}