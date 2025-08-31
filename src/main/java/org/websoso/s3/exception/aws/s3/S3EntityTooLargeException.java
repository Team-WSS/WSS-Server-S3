package org.websoso.s3.exception.aws.s3;
/**
 * S3에 업로드하려는 객체의 크기가 허용된 최대치를 초과했을 때 발생하는 예외입니다.
 */
public class S3EntityTooLargeException extends S3OperationException {
    public S3EntityTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public S3EntityTooLargeException(String message, Throwable cause, String awsErrorCode, String awsRequestId, String awsExtendedRequestId, int httpStatusCode) {
        super(message, cause, awsErrorCode, awsRequestId, awsExtendedRequestId, httpStatusCode);
    }
}
