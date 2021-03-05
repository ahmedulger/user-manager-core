package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.core.password.PasswordEncoder;
import com.ulger.sk.usermanager.api.user.validation.UserValidationContext;
import com.ulger.sk.usermanager.api.user.validation.UserValidationResult;
import com.ulger.sk.usermanager.api.user.validation.ValidationException;
import com.ulger.sk.usermanager.exception.IllegalParameterException;
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

    /**
     * Constructs instance with UserDao.
     * If no event listener given than any events will be published after user modification
     * @param userDao
     */
    public DefaultUserManager(PasswordEncoder passwordEncoder, UserValidationContext userValidationContext, UserDao userDao) {
        this.userValidationContext = userValidationContext;
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
        init();
    }

    private final void init() {
        if (passwordEncoder == null) {
            logger.error("[init] No password encoder found, PasswordEncoder is required");
            throw new IllegalArgumentException("No password encoder found, PasswordEncoder is required");
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
        try {
            logger.info("[createUser] User is creating :: data={}", modificationData);

            // Validate
            MutableUserAdapter mutableUserAdapter = new MutableUserAdapter(modificationData);
            validate(mutableUserAdapter, UserOperation.CREATE);

            // Update user's raw password to the hashed password
            encryptPassword(mutableUserAdapter);

            User user = userDao.create(mutableUserAdapter);
            logger.info("[createUser] User has been created");

            return user;

        } catch (Exception e) {
            logger.error("Unable to create user", e);
            throw new UserOperationException("Unable to create user", e);
        }
    }

    @Override
    public User updateUser(UserModificationData modificationData) {
        try {
            logger.info("[updateUser] User is updating :: data={}", modificationData);
            validate(modificationData, UserOperation.UPDATE);

            User user = userDao.update(modificationData);
            logger.info("[updateUser] User has been updated :: username={}", modificationData.getUsername());

            return user;
        } catch (Exception e) {
            logger.error("Unable to update user", e);
            throw new UserOperationException("Unable to update user", e);
        }
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        try {
            logger.info("[changePassword] User's password is updating :: email={}", email);

            if (Objects.isNull(email)) {
                throw new IllegalParameterException(UserField.EMAIL.getName(), "email should be given");
            }

            MutableUserAdapter mutableUserAdapter = new MutableUserAdapter();
            mutableUserAdapter.setRawPassword(newPassword);
            validate(mutableUserAdapter, UserOperation.CHANGE_PASSWORD);

            User user = userDao.findByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("User not found with email: " + email);
            }

            if (!passwordEncoder.matches(oldPassword, user.getCredential())) {
                throw new IllegalParameterException(UserField.PASSWORD.getName(), "Password must be different with older");
            }

            // Update user's raw password to the hashed password
            encryptPassword(mutableUserAdapter);

            userDao.updatePasswordByUsername(user.getUsername(), mutableUserAdapter.getHashPassword());

            logger.info("[changePassword] User's password has been changed successfully :: email={}", user.getEmail());

        } catch (Exception e) {
            logger.error("Unable to change password", e);
            throw new UserOperationException("Unable to change password", e);
        }
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