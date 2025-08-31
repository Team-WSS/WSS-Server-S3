package org.websoso.s3.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websoso.s3.exception.aws.s3.*;
import org.websoso.s3.modle.Bucket;
import org.websoso.s3.modle.S3UploadResponse;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.InputStream;

class S3Uploader {

    private static final Logger log = LoggerFactory.getLogger(S3Uploader.class);

    private final S3Client s3Client;
    private final Bucket bucket;

    public S3Uploader(S3Client s3Client, Bucket bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public S3UploadResponse upload(ObjectKey objectKey, File file) {

        log.debug("Uploading file to S3: bucket={}, objectKey={}, file={}", bucket, objectKey.getValue(), file.getName());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket.getValue())
                    .key(objectKey.getValue())
                    .contentLength(file.length())
                    .build();
            RequestBody requestBody = RequestBody.fromFile(file);

            PutObjectResponse response = s3Client.putObject(putObjectRequest, requestBody);

            log.info("Successfully uploaded file to S3: bucket={}, objectKey={}", bucket, objectKey.getValue());

            return S3UploadResponse.from(response);

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
        // Unreachable, as handlers always throw an exception
        return null;
    }

    public S3UploadResponse upload(ObjectKey objectKey, File file, ContentType contentType) {

        log.debug("Uploading file to S3: bucket={}, objectKey={}, file={}, contentType={}", bucket, objectKey.getValue(), file.getName(), contentType.getValue());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket.getValue())
                    .key(objectKey.getValue())
                    .contentType(contentType.getValue())
                    .contentLength(file.length())
                    .build();
            RequestBody requestBody = RequestBody.fromFile(file);

            PutObjectResponse response = s3Client.putObject(putObjectRequest, requestBody);

            log.info("Successfully uploaded file to S3: bucket={}, objectKey={}", bucket, objectKey.getValue());

            return S3UploadResponse.from(response);

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
        // Unreachable, as handlers always throw an exception
        return null;
    }

    public S3UploadResponse upload(ObjectKey objectKey, InputStream inputStream, ContentType contentType, ContentLength contentLength) {

        log.debug("Uploading input stream to S3: bucket={}, objectKey={}, contentType={}, contentLength={}", bucket, objectKey.getValue(), contentType.getValue(), contentLength.getValue());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket.getValue())
                    .key(objectKey.getValue())
                    .contentType(contentType.getValue())
                    .contentLength(contentLength.getValue())
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(inputStream, contentLength.getValue());
            PutObjectResponse response = s3Client.putObject(putObjectRequest, requestBody);

            log.info("Successfully uploaded to S3: bucket={}, objectKey={}", bucket, objectKey.getValue());

            return S3UploadResponse.from(response);

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
        // Unreachable, as handlers always throw an exception
        return null;
    }


    private void handleS3Exception(S3Exception e, ObjectKey objectKey) {
        AwsErrorDetails errorDetails = e.awsErrorDetails();
        String errorCode = errorDetails.errorCode();
        String errorMessage = errorDetails.errorMessage();
        int statusCode = e.statusCode();
        String requestId = e.requestId();
        String extendedRequestId = e.extendedRequestId();

        log.error("S3 service error during upload. ObjectKey: {}, HTTP Status: {}, AWS Error Code: {}, Message: {}, AWS Request ID: {}",
                objectKey.getValue(), statusCode, errorCode, errorMessage, requestId, e);

        S3AwsErrorCodes code = S3AwsErrorCodes.fromCode(errorCode);
        switch (code) {
            case NO_SUCH_BUCKET:
                throw new S3BucketNotFoundException("S3 Bucket not found: " + bucket.getValue(), e, errorCode, requestId, extendedRequestId, statusCode);
            case ACCESS_DENIED:
            case ACCOUNT_PROBLEM:
            case ALL_ACCESS_DISABLED:
                throw new S3AccessDeniedException("S3 Access denied for key '" + objectKey.getValue() + "'. Check put permissions.", e, errorCode, requestId, extendedRequestId, statusCode);
            case ENTITY_TOO_LARGE:
                throw new S3EntityTooLargeException("Upload failed because the file size for key '" + objectKey.getValue() + "' exceeds the limit.", e, errorCode, requestId, extendedRequestId, statusCode);
            default:
                throw new S3OperationException("Failed to upload S3 object for key '" + objectKey.getValue() + "'. The AWS Error Code is " + code + ".", e, errorCode, requestId, extendedRequestId, statusCode);
        }
    }

    private void handleSdkServiceException(SdkServiceException e, ObjectKey objectKey) {
        int statusCode = e.statusCode();
        String requestId = e.requestId();
        String extendedRequestId = e.extendedRequestId();
        log.error("S3 service error during upload. ObjectKey: {}, HTTP Status: {}, AWS Request ID: {}",
                objectKey.getValue(), statusCode, requestId, e);

        throw new S3OperationException("Failed to upload S3 object for key '" + objectKey.getValue() + "' due to a generic S3 service error.", e, null, requestId, extendedRequestId, statusCode);
    }

    private void handleSdkClientException(SdkClientException e, ObjectKey objectKey) {
        log.error("S3 Client-side error during upload for key: {}", objectKey.getValue(), e);
        throw new S3OperationException("Failed to communicate with S3 to upload for key: " + objectKey.getValue(), e);
    }

    private void handleSdkException(SdkException e, ObjectKey objectKey) {
        log.error("An unexpected SDK error during upload for key: {}", objectKey.getValue(), e);
        throw new S3OperationException("An unexpected error occurred within the AWS SDK during upload.", e);
    }

}