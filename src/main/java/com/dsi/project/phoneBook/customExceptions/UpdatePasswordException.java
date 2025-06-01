package com.dsi.project.phoneBook.customExceptions;

public class UpdatePasswordException extends RuntimeException {
    public UpdatePasswordException(String message) {
        super(message);
    }
}
