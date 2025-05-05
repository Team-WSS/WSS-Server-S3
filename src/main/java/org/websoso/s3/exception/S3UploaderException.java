package org.websoso.s3.exception;

public class S3UploaderException extends RuntimeException {
    public S3UploaderException(String message) {
        super(message);
    }

    public S3UploaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
