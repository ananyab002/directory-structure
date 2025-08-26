package com.directorystructure.exceptions;

/**
 * Exception for data parsing errors
 */
public class DataParsingException extends RuntimeException {
    public DataParsingException(String message) {
        super(message);
    }
    
    public DataParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}