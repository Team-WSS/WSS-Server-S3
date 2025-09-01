package org.websoso.s3.exception.validation;

public class InvalidFileException extends ValidationException {
    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }
}