package org.websoso.s3.modle;

public record S3UploadResult(
        boolean isSuccess,
        String eTag,
        String url,
        String message
) {
    public static S3UploadResult success(S3UploadResponse response, String url) {
        return new S3UploadResult(true, "", response.eTag(), url);
    }

    public static S3UploadResult fail(S3UploadResponse response) {
        return new S3UploadResult(false, "S3 upload fail, status code: " + response.statusCode() + ", message: " + response.statusText(), "", "");
    }
}