package com.ulger.sk.usermanager.apiresult;

public interface ServiceResult {

    boolean isValid();

    ErrorCollection getErrorCollection();
}
