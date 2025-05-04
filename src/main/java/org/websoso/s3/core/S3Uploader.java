package org.websoso.s3.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websoso.s3.exception.S3UploaderException;
import org.websoso.s3.modle.S3UploadResponse;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.InputStream;

public class S3Uploader {

    private static final Logger log = LoggerFactory.getLogger(S3Uploader.class);

    private final S3Client s3Client;
    private final String bucket;

    public S3Uploader(S3Client s3Client, String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public S3UploadResponse upload(String key, File file) {

        log.debug("Uploading file to S3: bucket={}, key={}, file={}", bucket, key, file.getName());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentLength(file.length())
                    .build();
            RequestBody requestBody = RequestBody.fromFile(file);

            PutObjectResponse response = s3Client.putObject(putObjectRequest, requestBody);

            log.info("Successfully uploaded file to S3: bucket={}, key={}", bucket, key);

            return S3UploadResponse.from(response);

        } catch (Exception e) {
            throw new S3UploaderException("S3 file upload failed: " + e.getMessage(), e);
        }
    }

    public S3UploadResponse upload(String key, File file, String contentType) {

        log.debug("Uploading file to S3: bucket={}, key={}, file={}, contentType={}", bucket, key, file.getName(), contentType);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(file.length())
                    .build();
            RequestBody requestBody = RequestBody.fromFile(file);

            PutObjectResponse response = s3Client.putObject(putObjectRequest, requestBody);

            log.info("Successfully uploaded file to S3: bucket={}, key={}", bucket, key);

            return S3UploadResponse.from(response);

        } catch (Exception e) {
            throw new S3UploaderException("S3 file upload failed: " + e.getMessage(), e);
        }
    }

    public S3UploadResponse upload(String key, InputStream inputStream, String contentType, long contentLength) {

        log.debug("Uploading input stream to S3: bucket={}, key={}, contentType={}, contentLength={}", bucket, key, contentType, contentLength);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(inputStream, contentLength);
            PutObjectResponse response = s3Client.putObject(putObjectRequest, requestBody);

            log.info("Successfully uploaded to S3: bucket={}, key={}", bucket, key);

            return S3UploadResponse.from(response);

        } catch (Exception e) {
            throw new S3UploaderException("S3 upload failed: " + e.getMessage(), e);
        }
    }

}