package org.websoso.s3.exception.validation;

public class InvalidAccessKeyException extends RuntimeException {

    public InvalidAccessKeyException(String message) {
        super(message);
    }

    public InvalidAccessKeyException(String message, Throwable cause) {
        super(message, cause);
    }

}
