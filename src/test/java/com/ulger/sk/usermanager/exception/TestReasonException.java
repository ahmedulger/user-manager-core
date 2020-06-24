package com.ulger.sk.usermanager.exception;

public class TestReasonException extends RuntimeException {

    private int reason;

    public TestReasonException(int reason, String message) {
        super(message);
        this.reason = reason;
    }

    public int getReason() {
        return reason;
    }

    public static class Reason {
        public static final int INCORRECT_RESULT_COUNT = 1;
        public static final int FIELD_SET = 2;
        public static final int UNIQUE_FIELD = 3;
    }
}
