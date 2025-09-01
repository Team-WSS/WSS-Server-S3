package org.websoso.s3.exception.aws.s3;

/**
 * S3에 접근 권한이 없을 때 발생하는 예외입니다.
 */
public class S3AccessDeniedException extends S3OperationException {

    public S3AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public S3AccessDeniedException(String message, Throwable cause, String awsErrorCode, String awsRequestId, String awsExtendedRequestId, int httpStatusCode) {
        super(message, cause, awsErrorCode, awsRequestId, awsExtendedRequestId, httpStatusCode);
    }
}