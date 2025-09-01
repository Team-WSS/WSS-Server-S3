package org.websoso.s3.exception.aws.s3;

/**
 * S3 버킷을 찾을 수 없을 때 발생하는 예외입니다.
 */
public class S3BucketNotFoundException extends S3OperationException {
    public S3BucketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public S3BucketNotFoundException(String message, Throwable cause, String awsErrorCode, String awsRequestId, String awsExtendedRequestId, int httpStatusCode) {
        super(message, cause, awsErrorCode, awsRequestId, awsExtendedRequestId, httpStatusCode);
    }
}
