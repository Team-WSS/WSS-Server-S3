package org.websoso.s3.exception.validation;

public class InvalidContentTypeException extends ValidationException {

    public InvalidContentTypeException(String message) {
        super(message);
    }

    public InvalidContentTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}
