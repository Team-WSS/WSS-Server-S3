package org.websoso.s3.exception.validation;

public class InvalidContentLengthException extends RuntimeException {

    public InvalidContentLengthException(String message) {
        super(message);
    }

    public InvalidContentLengthException(String message, Throwable cause) {
        super(message, cause);
    }

}
