package com.santik.bookchecker.exception;

public class MissingAnswersException extends RuntimeException {
    public MissingAnswersException(String message) {
        super(message);
    }
}
