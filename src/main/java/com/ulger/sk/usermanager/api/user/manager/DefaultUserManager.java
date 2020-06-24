package com.ulger.sk.usermanager.api.user.manager;

import com.ulger.sk.usermanager.api.user.*;
import com.ulger.sk.usermanager.api.user.model.User;
import com.ulger.sk.usermanager.api.user.model.UserFields;
import com.ulger.sk.usermanager.api.user.model.UserImp;
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
import java.util.stream.Collectors;

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
     * @param userEntityBuilder is useful to save waste converting operation between type of {@link UserEntity}
     *                          {@link UserDao} uses a {@link UserEntity} instance to save data to the source.
     *                          If no userEntityBuilder given than this manager passes {@link DefaultUserEntity} to {@link UserDao}.
     *                          But if you give a {@link UserEntityBuilder} that creates a {@link UserEntity} directly without
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
     * @param userEntityBuilder is useful to save waste converting operation between type of {@link UserEntity}
     *                        {@link UserDao} uses a {@link UserEntity} instance to save data to the source.
     *                        If no userEntityBuilder given than this manager passes {@link DefaultUserEntity} to {@link UserDao}.
     *                        But if you give a {@link UserEntityBuilder} that creates a {@link UserEntity} directly without
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

        UserEntity userEntity = userDao.findByEmail(email);

        if (userEntity == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("[getUserByEmail] No user found with email :: email={}", email);
            }

            return null;
        }

        return convertEntity(userEntity);
    }

    /**
     * @return All users. If no user found than return empty list
     */
    @Override
    public List<User> getAllUsers() {
        List<UserEntity> allUsers = userDao.find();

        if (logger.isDebugEnabled()) {
            logger.debug("[getAllUsers] found user count :: count={}", allUsers.size());
        }

        return allUsers
                .stream()
                .map((entity) -> convertEntity(entity))
                .collect(Collectors.toList());
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

        UserEntity userEntity = getUserByIdOrEmail(userModificationData);
        if (userEntity == null) {
            throw new UserNotFoundException("User not found with id: " +
                    mutableUserModificationData.getId() + " or email: " + mutableUserModificationData.getEmail());
        }

        checkIfEmailChanged(userModificationData, userEntity);

        mutableUserModificationData.setId(userEntity.getId());

        User user = createOrUpdateUser(mutableUserModificationData);
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

        UserEntity userEntity = getUserByIdOrEmail(userModificationData);
        if (userEntity == null) {
            throw new UserNotFoundException("User not found with id: " +
                    mutableUserModificationData.getId() + " or email: " + mutableUserModificationData.getEmail());
        }

        checkIfEmailChanged(userModificationData, userEntity);
        encryptPassword(mutableUserModificationData);

        mutableUserModificationData.setId(userEntity.getId());

        User user = createOrUpdateUser(mutableUserModificationData);
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

    private UserEntity getUserByIdOrEmail(UserModificationData userModificationData) {
        if (!Objects.isNull(userModificationData.getId())) {
            return userDao.findById(userModificationData.getId());
        }

        return userDao.findByEmail(userModificationData.getEmail());
    }

    private void checkIfEmailChanged(UserModificationData userModificationData, UserEntity userEntity) {
        if (!Objects.isNull(userModificationData.getId()) &&
                !StringUtils.isBlank(userModificationData.getEmail()) &&
                !userModificationData.getEmail().equals(userEntity.getEmail())) {

            throw new ApiException("Email is one of none-updatable fields");
        }
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

    private User createOrUpdateUser(UserModificationData modificationData) {
        logger.info("[createOrUpdateUser] User is saving :: data={}", modificationData);

        UserEntity userEntity;

        if (userEntityBuilder == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("[createOrUpdateUser] No entity builder found, using default");
            }

            userEntity = modificationData;
        } else {
            userEntity = userEntityBuilder.build(modificationData);
        }

        userEntity = userDao.save(userEntity);
        logger.info("[createOrUpdateUser] User has been saved");

        User user = UserImp.newInstance(userEntity.getId(), userEntity.getEmail(), userEntity.getFirstName(), userEntity.getLastName());
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

        modificationEventListeners.forEach((listener) -> {
            listener.onModified(event);
        });
    }

    private UserImp convertEntity(UserEntity userEntity) {
        return UserImp.newInstance(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName());
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