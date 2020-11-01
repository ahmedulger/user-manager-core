package com.ulger.sk.usermanager.api.user.password;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for managing errors
 */
public class SimplePasswordCheckingResult implements PasswordCheckingResult {

    private List<String> errors;

    public SimplePasswordCheckingResult() {
        this.errors = new LinkedList<>();
    }

    @Override
    public boolean hasError() {
        return !errors.isEmpty();
    }

    @Override
    public void addError(String error) {
        this.errors.add(error);
    }

    @Override
    public void addError(Collection<String> errors) {
        this.errors.addAll(errors);
    }

    @Override
    public Collection<String> getErrors() {
        return errors;
    }

}