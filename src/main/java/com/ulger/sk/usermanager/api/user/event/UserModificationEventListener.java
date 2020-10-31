package com.ulger.sk.usermanager.api.user.event;

public interface UserModificationEventListener {

    void onModified(UserModificationEvent event);

    boolean isAsync();
}