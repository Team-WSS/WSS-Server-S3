package org.websoso.s3.exception.aws.global;

public class AwsCredentialsNotFoundException extends AwsGlobalException {

    public AwsCredentialsNotFoundException(String message) {
        super(message);
    }

    public AwsCredentialsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}