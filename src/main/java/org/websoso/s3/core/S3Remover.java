package org.websoso.s3.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

public class S3Remover {

    private static final Logger log = LoggerFactory.getLogger(S3Remover.class);

    private final S3Client s3Client;
    private final String bucket;

    public S3Remover(S3Client s3Client, String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public boolean delete(String key) {

        log.debug("Deleting object from S3: bucket={}, key={}", bucket, key);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("Successfully deleted object from S3: bucket={}, key={}", bucket, key);

            return true;

        } catch (Exception e) {
            log.error("Failed to delete object from S3: bucket={}, key={}", bucket, key, e);
            return false;
        }
    }

}
