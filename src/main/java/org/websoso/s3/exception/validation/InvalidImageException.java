package org.websoso.s3.exception.validation;

public class InvalidImageException extends ValidationException {
    public InvalidImageException(String message) {
        super(message);
    }

    public InvalidImageException(String message, Throwable cause) {
        super(message, cause);
    }
}