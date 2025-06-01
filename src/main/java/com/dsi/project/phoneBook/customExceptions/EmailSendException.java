package com.dsi.project.phoneBook.customExceptions;

public class EmailSendException extends RuntimeException {
    public EmailSendException(String message) {
        super(message);
    }
}
