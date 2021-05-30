package com.ulger.sk.usermanager.api.user.core;

import com.ulger.sk.usermanager.api.user.core.password.PasswordEncoder;
import com.ulger.sk.usermanager.api.user.validation.UserValidationResult;
import com.ulger.sk.usermanager.api.user.validation.UserValidatorPicker;
import com.ulger.sk.usermanager.api.user.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Simple implementation of {@link UserManager}
 */
public class DefaultUserManager implements UserManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserManager.class);

    private UserValidatorPicker userValidatorPicker;
    private PasswordEncoder passwordEncoder;
    private UserDao userDao;

    public DefaultUserManager(UserValidatorPicker userValidatorPicker, PasswordEncoder passwordEncoder, UserDao userDao) {
        this.userValidatorPicker = userValidatorPicker;
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
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

        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email must given");
        }

        Optional<User> user = userDao.findByEmail(email);

        if (user.isPresent()) {
            return user.get();
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
        return userDao.find();
    }

    @Override
    public User createUser(UserModificationData modificationData) {
        // Validate
        validate(modificationData, UserOperation.CREATE);

        MutableUserAdapter mutableUserAdapter = new MutableUserAdapter(modificationData);

        // Update user's raw password to the hashed password
        mutableUserAdapter.setHashPassword(encryptRawPassword(mutableUserAdapter.getRawPassword()));

        User user = userDao.create(mutableUserAdapter);
        logger.info("[createUser] User has been created :: username={}", user.getUsername());

        return user;
    }

    @Override
    public User updateUser(UserModificationData modificationData) {
        validate(modificationData, UserOperation.UPDATE);

        User user = userDao.update(modificationData);
        logger.info("[updateUser] User has been updated :: username={}", modificationData.getUsername());

        return user;
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        logger.info("[changePassword] User's password is updating :: email={}", email);

        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email should be given");
        }

        MutableUserAdapter mutableUserAdapter = new MutableUserAdapter();
        mutableUserAdapter.setRawPassword(newPassword);
        validate(mutableUserAdapter, UserOperation.CHANGE_PASSWORD);

        User user = userDao
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (passwordEncoder.matches(oldPassword, user.getCredential())) {
            throw new IllegalArgumentException("Password must be different with older");
        }

        // Update user's raw password to the hashed password
        mutableUserAdapter.setHashPassword(encryptRawPassword(newPassword));

        userDao.updatePasswordByUsername(user.getUsername(), mutableUserAdapter.getHashPassword());

        logger.info("[changePassword] User's password has been changed successfully :: email={}", user.getEmail());
    }

    private String encryptRawPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private void validate(UserModificationData userModificationData, UserOperation operation) {
        UserValidationResult validationResult = userValidatorPicker.pick(operation).validate(userModificationData);

        if (!validationResult.isValid()) {
            logger.info("[validate] User is not valid :: validationResult={}", validationResult);
            throw new ValidationException(validationResult.getErrorBag());
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("userValidatorPicker", userValidatorPicker)
                .append("passwordEncoder", passwordEncoder)
                .append("userDao", userDao)
                .toString();
    }
}