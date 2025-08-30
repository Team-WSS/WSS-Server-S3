package org.websoso.s3.exception.validation;

public class InvalidObjectKeyException extends RuntimeException {

    public InvalidObjectKeyException(String message) {
        super(message);
    }

    public InvalidObjectKeyException(String message, Throwable cause) {
      super(message, cause);
    }

}
