package org.websoso.s3.exception.validation;

public class InvalidContentLengthException extends ValidationException {

    public InvalidContentLengthException(String message) {
        super(message);
    }

    public InvalidContentLengthException(String message, Throwable cause) {
        super(message, cause);
    }

}
