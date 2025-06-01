package com.dsi.project.phoneBook.customExceptions;

public class UpdateContactException extends RuntimeException {
    public UpdateContactException(String message) {
        super(message);
    }
}
