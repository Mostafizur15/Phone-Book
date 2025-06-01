package com.dsi.project.phoneBook.customExceptions;

public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
    }
}
