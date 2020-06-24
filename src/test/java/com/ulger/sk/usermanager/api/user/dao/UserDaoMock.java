package com.ulger.sk.usermanager.api.user.dao;

import com.google.common.collect.Sets;
import com.ulger.sk.usermanager.api.user.manager.*;
import com.ulger.sk.usermanager.exception.DataAccessException;
import com.ulger.sk.usermanager.exception.TestReasonException;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserDaoMock implements UserDao<Integer> {

    private final AtomicInteger id = new AtomicInteger(0);
    private final Set<UserEntity> users = new HashSet();

    private UniqueFieldChecker<UserEntity, DefaultUserEntity> uniqueFieldChecker = new UniqueFieldChecker(DefaultUserEntity.class, "id", "email");
    private FieldUpdater<UserEntity, DefaultUserEntity> fieldUpdater = new FieldUpdater(DefaultUserEntity.class, "id");

    @Override
    public UserEntity findById(Integer id) {
        return getUniqueUser(user -> user.getId().equals(id));
    }

    @Override
    public UserEntity findByEmail(String email) {
        return getUniqueUser(user -> user.getEmail().equals(email));
    }

    @Override
    public List<UserEntity> find() throws DataAccessException {
        return users.stream().collect(Collectors.toList());
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        if (userEntity == null) {
            throw new IllegalArgumentException("Target object must not bu null");
        }

        Object id = userEntity.getId();
        UserEntity sourceData = getUniqueUser(user -> user.getId().equals(id));

        if (userEntity instanceof UserModificationData) {
            userEntity = getFromModificationData((UserModificationData) userEntity);
        }

        saveInternal(sourceData, userEntity);

        return userEntity;
    }

    private UserEntity getFromModificationData(UserModificationData userModificationData) {
        return new DefaultUserEntity(
                userModificationData.getId(),
                userModificationData.getEmail(),
                userModificationData.getFirstName(),
                userModificationData.getLastName(),
                userModificationData.getCredential());
    }

    private UserEntity getUniqueUser(Predicate<UserEntity> filter) {
        Stream<UserEntity> result = users.stream().filter(filter);

        if (users.stream().filter(filter).count() > 1) {
            throw new DataAccessException(new TestReasonException(TestReasonException.Reason.INCORRECT_RESULT_COUNT, "Incorrect result count"));
        }

        if (users.stream().filter(filter).findFirst().isPresent()) {
            return result.findFirst().get();
        }

        return null;
    }

    private UserEntity saveInternal(UserEntity sourceData, UserEntity toBeSavedData) {
        // Throws exception if non unique field has detected
        uniqueFieldChecker.checkUniqueFields(users, toBeSavedData);

        if (sourceData == null) {
            sourceData = toBeSavedData;
            setId(toBeSavedData, generateId());
            users.add(sourceData);
        } else {
            // Update source object with new one
            fieldUpdater.updateFields(toBeSavedData, sourceData);
        }

        return sourceData;
    }

    private int generateId() {
        return id.addAndGet(1);
    }

    private void setId(UserEntity userEntity, Integer id) {
        if (userEntity instanceof DefaultUserEntity) {
            try {
                Field idField = DefaultUserEntity.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(userEntity, id);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Setting fields with name 'id' by using reflection is failed", e);
            }

            return;
        }

        try {
            Field idField = MutableUserModificationData.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(userEntity, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Setting fields with name 'id' by using reflection is failed", e);
        }
    }
}

class UniqueFieldChecker<C, S> {
    private final Logger logger = LoggerFactory.getLogger(UniqueFieldChecker.class);

    private Class<C> type;
    private Set<Field> fields;

    public UniqueFieldChecker(Class<C> type) {
        this.type = type;
    }

    public UniqueFieldChecker(Class<C> type, String...fieldNames) {
        this.type = type;
        loadFields(fieldNames);
    }

    public void checkUniqueFields(Collection<C> sourceRepository, C target) {
        if (CollectionUtils.isEmpty(sourceRepository)) {
            logger.info("Source repository is empty, passing unique field checking");
            return;
        }

        if (CollectionUtils.isEmpty(fields)) {
            logger.info("Unique fields list is empty, passing unique field checking");
            return;
        }

        if (target == null) {
            logger.info("Target object is null");
            return;
        }

        sourceRepository.forEach(source -> {
            if (source.equals(target)) {
                return;
            }

            fields.forEach(field -> {
                try {
                    Object sourceValue = field.get(source);
                    Object targetValue = field.get(target);

                    if (Objects.equals(sourceValue, targetValue)) {
                        throw new DataAccessException(new TestReasonException(TestReasonException.Reason.UNIQUE_FIELD, "Unique field insertion occurred on column: " + field.getName()));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Exception occurred while getting value of field");
                }
            });
        });
    }

    private void loadFields(String...fieldNames) {
        this.fields = new HashSet<>();

        Arrays.stream(fieldNames).forEach(field -> {
            try {
                Field fieldDef = type.getDeclaredField(field);
                fieldDef.setAccessible(true);
                this.fields.add(fieldDef);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new RuntimeException("Field not found with name: " + field, e);
            }
        });
    }
}

class FieldUpdater<C, S> {
    private final Logger logger = LoggerFactory.getLogger(FieldUpdater.class);

    private Class<C> type;
    private Collection<Field> fields;

    public FieldUpdater(Class<C> type, String...skippingFields) {
        this.type = type;
        this.fields = new HashSet<>();

        setFieldsPublic(skippingFields);
    }

    public void updateFields(C source, C target) {
        if (source == null || target == null) {
            logger.info("Target object or source object is null");
            return;
        }

        fields.forEach(field -> {
            try {
                field.set(target, field.get(source));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new DataAccessException(new TestReasonException(TestReasonException.Reason.FIELD_SET, "Exception occurred while setting value of field"));
            }
        });
    }

    private void setFieldsPublic(String...skippingFields) {
        Set<String> toBeSkippedFields = Sets.newHashSet(skippingFields);

        Stream.of(type.getDeclaredFields())
                .filter(field -> !toBeSkippedFields.contains(field.getName()))
                .forEach(field -> {
                    field.setAccessible(true);
                    fields.add(field);
                });
    }
}