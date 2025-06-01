package com.dsi.project.phoneBook.customExceptions;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }
}
