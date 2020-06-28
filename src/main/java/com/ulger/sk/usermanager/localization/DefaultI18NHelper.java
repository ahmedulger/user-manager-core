package com.ulger.sk.usermanager.localization;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class DefaultI18NHelper implements I18NHelper {

    private boolean failWhenNotFound;
    private ResourceBundle resourceBundle;

    public DefaultI18NHelper() {
        this.resourceBundle = ResourceBundle.getBundle("messages", Locale.getDefault());
    }

    @Override
    public String getMessage(String key) {
        return resourceBundle.getString(key);
    }

    @Override
    public String getMessage(String key, Locale locale) {
        return resourceBundle.getString(key);
    }

    @Override
    public String getMessage(String key, Object... params) {
        String message = StringUtils.defaultString(resourceBundle.getString(key), "");
        return new MessageFormat(message).format(params);
    }

    @Override
    public String getMessage(String key, Locale locale, Object... params) {
        String message = StringUtils.defaultString(resourceBundle.getString(key), "");
        return new MessageFormat(message).format(params);
    }
}