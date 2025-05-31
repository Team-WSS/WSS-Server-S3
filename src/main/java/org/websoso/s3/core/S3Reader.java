package org.websoso.s3.core;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

class S3Reader {

    private final S3Client s3Client;
    private final String bucket;

    public S3Reader(S3Client s3Client, String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public String getUrl(Key key) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket)
                .key(key.getValue())
                .build();

        return s3Client.utilities().getUrl(request).toString();
    }

}
