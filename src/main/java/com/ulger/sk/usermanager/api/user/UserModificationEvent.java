package com.ulger.sk.usermanager.api.user;

import com.ulger.sk.usermanager.api.user.manager.User;
import com.ulger.sk.usermanager.api.user.manager.UserModificationData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

/**
 * This class is used to throw information about user modification operation.
 */
public class UserModificationEvent {

    public enum EventType {
        CREATE("create"), UPDATE("update");

        private String value;

        EventType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private EventType eventType;
    private UserModificationData modificationData;
    private User modifiedData;
    private LocalDateTime localDateTime;

    public UserModificationEvent() {
    }

    public UserModificationEvent(EventType eventType, UserModificationData modificationData, User modifiedData, LocalDateTime localDateTime) {
        this.eventType = eventType;
        this.modificationData = modificationData;
        this.modifiedData = modifiedData;
        this.localDateTime = localDateTime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public UserModificationData getModificationData() {
        return modificationData;
    }

    public void setModificationData(UserModificationData modificationData) {
        this.modificationData = modificationData;
    }

    public User getModifiedData() {
        return modifiedData;
    }

    public void setModifiedData(User modifiedData) {
        this.modifiedData = modifiedData;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("eventType", eventType)
                .append("modificationData", modificationData)
                .append("modifiedData", modifiedData)
                .append("localDateTime", localDateTime)
                .toString();
    }
}