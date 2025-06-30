package com.rental.houserental.exceptions.property;

public class FileDeleteException extends RuntimeException {
    public FileDeleteException(String message) {
        super(message);
    }
    
    public FileDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
} 