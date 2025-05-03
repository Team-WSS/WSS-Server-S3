package org.websoso.s3.exception;

public class AwsRegionNotFoundException extends RuntimeException {

    public AwsRegionNotFoundException(String message) {
        super(message);
    }

    public AwsRegionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
