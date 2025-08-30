package org.websoso.s3.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websoso.s3.modle.Bucket;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

class S3Remover {

    private static final Logger log = LoggerFactory.getLogger(S3Remover.class);

    private final S3Client s3Client;
    private final Bucket bucket;

    public S3Remover(S3Client s3Client, Bucket bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

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

        } catch (Exception e) {
            log.error("Failed to delete object from S3: bucket={}, objectKey={}", bucket, objectKey.getValue(), e);
            return false;
        }
    }

}
