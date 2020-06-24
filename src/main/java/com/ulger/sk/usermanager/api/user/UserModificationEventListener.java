package com.ulger.sk.usermanager.api.user;

public interface UserModificationEventListener {

    void onModified(UserModificationEvent event);
}