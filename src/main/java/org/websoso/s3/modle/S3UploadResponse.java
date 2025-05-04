package org.websoso.s3.modle;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public record S3UploadResponse(
        boolean isSuccess,
        String eTag,
        int statusCode,
        String statusText
) {
    public static S3UploadResponse from(PutObjectResponse response) {
        return new S3UploadResponse(
                response.sdkHttpResponse().isSuccessful(),
                response.eTag(),
                response.sdkHttpResponse().statusCode(),
                response.sdkHttpResponse().statusText().orElse("")
        );
    }
}
