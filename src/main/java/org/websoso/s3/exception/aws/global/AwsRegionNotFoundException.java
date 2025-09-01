package org.websoso.s3.exception.aws.global;

public class AwsRegionNotFoundException extends AwsGlobalException {

    public AwsRegionNotFoundException(String message) {
        super(message);
    }

    public AwsRegionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
