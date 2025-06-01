package com.dsi.project.phoneBook.customExceptions;

public class AddFailedException extends RuntimeException {
    public AddFailedException(String message) {
        super(message);
    }
}
