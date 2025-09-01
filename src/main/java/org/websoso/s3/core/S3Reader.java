package org.websoso.s3.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websoso.s3.exception.aws.s3.S3AccessDeniedException;
import org.websoso.s3.exception.aws.s3.S3AwsErrorCodes;
import org.websoso.s3.exception.aws.s3.S3BucketNotFoundException;
import org.websoso.s3.exception.aws.s3.S3ObjectKeyNotFoundException;
import org.websoso.s3.exception.aws.s3.S3OperationException;
import org.websoso.s3.modle.Bucket;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;


class S3Reader {

    private static final Logger log = LoggerFactory.getLogger(S3Reader.class);

    private final S3Client s3Client;
    private final Bucket bucket;

    public S3Reader(S3Client s3Client, Bucket bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    /**
     * S3 객체의 URL을 반환합니다.
     *
     * @param objectKey S3 객체 키
     * @return 객체 URL 문자열
     * @throws S3BucketNotFoundException    버킷이 존재하지 않는 경우
     * @throws S3ObjectKeyNotFoundException 객체 키가 존재하지 않는 경우
     * @throws S3OperationException         기타 S3 SDK 서비스 오류 또는 S3 SDK 클라이언트 측 오류 발생 시
     */
    public String getUrl(ObjectKey objectKey) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket.getValue())
                .key(objectKey.getValue())
                .build();
        try {
            return s3Client.utilities().getUrl(request).toString();

        } catch (S3Exception e) {
            // AWS SDK Service 중 S3 에러
            handleS3Exception(e, objectKey);

        } catch (SdkServiceException e) {
            // AWS SDK Service 최상위 에러
            handleSdkServiceException(e, objectKey);

        } catch (SdkClientException e) {
            // AWS SDK Client 최상위 에러
            handleSdkClientException(e, objectKey);

        } catch (SdkException e) {
            // AWS SDK 최상위 에러
            handleSdkException(e, objectKey);
        }

        return null; // Unreachable, as handlers always throw
    }

    private void handleS3Exception(S3Exception e, ObjectKey objectKey) {
        AwsErrorDetails errorDetails = e.awsErrorDetails();
        String errorCode = errorDetails.errorCode();
        String errorMessage = errorDetails.errorMessage();
        int statusCode = e.statusCode();
        String requestId = e.requestId();
        String extendedRequestId = e.extendedRequestId();

        log.error("S3 service error getting URL. HTTP Status: {}, AWS Error Code: {}, Message: {}, AWS Request ID: {}",
                statusCode, errorCode, errorMessage, requestId, e);

        S3AwsErrorCodes code = S3AwsErrorCodes.fromCode(errorCode);
        switch (code) {
            case NO_SUCH_BUCKET:
                throw new S3BucketNotFoundException("S3 Bucket not found: " + bucket.getValue(), e, errorCode, requestId, extendedRequestId, statusCode);
            case NO_SUCH_KEY:
                throw new S3ObjectKeyNotFoundException("Object Key '" + objectKey.getValue() + "' not found in bucket '" + bucket.getValue() + "'", e, errorCode, requestId, extendedRequestId, statusCode);
            case ACCESS_DENIED:
            case ACCOUNT_PROBLEM:
            case ALL_ACCESS_DISABLED:
                throw new S3AccessDeniedException("S3 Access denied for key '" + objectKey.getValue() + "' with AWS Request ID '" + requestId + "'. Check permissions.", e, errorCode, requestId, extendedRequestId, statusCode);
            default:
                throw new S3OperationException("Failed to get URL for key '" + objectKey.getValue() + "'. The AWS Error Code is " + code + ".", e, errorCode, requestId, extendedRequestId, statusCode);
        }
    }

    private void handleSdkServiceException(SdkServiceException e, ObjectKey objectKey) {
        int statusCode = e.statusCode();
        String requestId = e.requestId();
        String extendedRequestId = e.extendedRequestId();
        log.error("S3 service error getting URL. HTTP Status: {}, AWS Request ID: {}",
                statusCode, requestId, e);
        throw new S3OperationException("Failed to get URL for key '" + objectKey.getValue() + "' due to an unhandled S3 service error.", e, null, requestId, extendedRequestId, statusCode);
    }

    private void handleSdkClientException(SdkClientException e, ObjectKey objectKey) {
        log.error("S3 Client-side error getting URL from S3 for key: {}",
                objectKey.getValue(), e);
        throw new S3OperationException("Failed to communicate with S3 to get URL for key: " + objectKey.getValue(), e);
    }

    private void handleSdkException(SdkException e, ObjectKey objectKey) {
        log.error("An unexpected SDK error getting URL from S3 for key: {}",
                objectKey.getValue(), e);
        throw new S3OperationException("An unexpected error occurred within the AWS SDK.", e);
    }
}
