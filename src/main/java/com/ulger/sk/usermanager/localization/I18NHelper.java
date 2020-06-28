package com.ulger.sk.usermanager.localization;

import java.util.Locale;

public interface I18NHelper {

    String getMessage(String key);

    String getMessage(String key, Locale locale);

    String getMessage(String key, Object...params);

    String getMessage(String key, Locale locale, Object...params);
}