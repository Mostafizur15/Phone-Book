package com.dsi.project.phoneBook.customExceptions;

public class DeleteContactException extends RuntimeException {
    public DeleteContactException(String message) {
        super(message);
    }
}
