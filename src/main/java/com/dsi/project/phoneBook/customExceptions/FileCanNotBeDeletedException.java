package com.dsi.project.phoneBook.customExceptions;

import java.io.IOException;

public class FileCanNotBeDeletedException extends IOException {
    public FileCanNotBeDeletedException(String message) {
        super(message);
    }
}
