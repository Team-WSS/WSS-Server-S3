package org.websoso.s3.exception;

public class AwsCredentialsNotFoundException extends RuntimeException {

    public AwsCredentialsNotFoundException(String message) {
        super(message);
    }

    public AwsCredentialsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}