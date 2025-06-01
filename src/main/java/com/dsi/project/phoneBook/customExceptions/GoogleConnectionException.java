package com.dsi.project.phoneBook.customExceptions;

public class GoogleConnectionException extends RuntimeException {
    public GoogleConnectionException(String message) {
        super(message);
    }
}
