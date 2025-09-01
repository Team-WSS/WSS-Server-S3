package org.websoso.s3.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websoso.s3.exception.aws.s3.*;
import org.websoso.s3.modle.Bucket;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

class S3Remover {

    private static final Logger log = LoggerFactory.getLogger(S3Remover.class);

    private final S3Client s3Client;
    private final Bucket bucket;

    public S3Remover(S3Client s3Client, Bucket bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    /**
     * S3 객체를 삭제합니다.
     * <p>
     * 성공 시 {@code true}를 반환하고, 실패 시 원인에 맞는 구체적인 {@link S3OperationException}의 하위 예외를 발생시킵니다.
     * S3의 정책에 따라, 삭제하려는 객체가 존재하지 않아도 성공으로 간주됩니다.
     * </p>
     *
     * @param objectKey 삭제할 S3 객체 키
     * @return 삭제 성공 시 {@code true}
     * @throws S3BucketNotFoundException 버킷을 찾을 수 없는 경우
     * @throws S3AccessDeniedException   삭제 권한이 없는 경우
     * @throws S3OperationException      기타 S3 서비스 오류 또는 클라이언트 측 통신 오류 발생 시
     */
    public boolean delete(ObjectKey objectKey) {

        log.debug("Deleting object from S3: bucket={}, objectKey={}", bucket, objectKey.getValue());

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket.getValue())
                    .key(objectKey.getValue())
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("Successfully deleted object from S3: bucket={}, objectKey={}", bucket, objectKey.getValue());

            return true;

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
        return false; // Unreachable, as handlers always throw
    }

    private void handleS3Exception(S3Exception e, ObjectKey objectKey) {
        AwsErrorDetails errorDetails = e.awsErrorDetails();
        String errorCode = errorDetails.errorCode();
        String errorMessage = errorDetails.errorMessage();
        int statusCode = e.statusCode();
        String requestId = e.requestId();
        String extendedRequestId = e.extendedRequestId();

        log.error("S3 service error during delete. ObjectKey: {}, HTTP Status: {}, AWS Error Code: {}, Message: {}, AWS Request ID: {}",
                objectKey.getValue(), statusCode, errorCode, errorMessage, requestId, e);

        S3AwsErrorCodes code = S3AwsErrorCodes.fromCode(errorCode);
        switch (code) {
            case NO_SUCH_BUCKET:
                throw new S3BucketNotFoundException("S3 Bucket not found: " + bucket.getValue(), e, errorCode, requestId, extendedRequestId, statusCode);
            case ACCESS_DENIED:
            case ACCOUNT_PROBLEM:
            case ALL_ACCESS_DISABLED:
                throw new S3AccessDeniedException("S3 Access denied for key '" + objectKey.getValue() + "'. Check delete permissions.", e, errorCode, requestId, extendedRequestId, statusCode);
            default:
                throw new S3OperationException("Failed to delete S3 object for key '" + objectKey.getValue() + "'. The AWS Error Code is " + code + ".", e, errorCode, requestId, extendedRequestId, statusCode);
        }
    }

    private void handleSdkServiceException(SdkServiceException e, ObjectKey objectKey) {
        int statusCode = e.statusCode();
        String requestId = e.requestId();
        String extendedRequestId = e.extendedRequestId();

        log.error("S3 service error during delete S3 object. ObjectKey: {}, HTTP Status: {}, AWS Request ID: {}",
                objectKey.getValue(), statusCode, requestId, e);

        throw new S3OperationException("Failed to delete S3 object for key '" + objectKey.getValue() + "' due to a generic S3 service error.", e, null, requestId, extendedRequestId, statusCode);
    }

    private void handleSdkClientException(SdkClientException e, ObjectKey objectKey) {
        log.error("S3 Client-side error during delete S3 object for key: {}", objectKey.getValue(), e);
        throw new S3OperationException("Failed to communicate with S3 to delete for key: " + objectKey.getValue(), e);
    }

    private void handleSdkException(SdkException e, ObjectKey objectKey) {
        log.error("An unexpected SDK error during delete S3 object for key: {}", objectKey.getValue(), e);
        throw new S3OperationException("An unexpected error occurred within the AWS SDK during delete S3 object.", e);
    }

}