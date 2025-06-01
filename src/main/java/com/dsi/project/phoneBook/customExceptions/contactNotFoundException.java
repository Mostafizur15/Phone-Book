package com.dsi.project.phoneBook.customExceptions;

public class contactNotFoundException extends RuntimeException {
    public contactNotFoundException(String message) {
        super(message);
    }
}
