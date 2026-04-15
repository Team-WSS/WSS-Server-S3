package org.websoso.s3.core;

import org.websoso.s3.exception.validation.InvalidFileException;
import org.websoso.s3.modle.Bucket;
import org.websoso.s3.modle.S3UploadResponse;
import org.websoso.s3.modle.S3UploadResult;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.InputStream;

/**
 * S3 파일 업로드 및 삭제를 위한 S3DefaultService 인터페이스의 구현체 입니다.
 * <p>
 * 다양한 타입의 파일 입력(File, InputStream 등)을 지원하며,
 * 업로드 결과는 {@link S3UploadResult}로 반환됩니다.
 * </p>
 */
public class S3FileService implements S3DefaultService {

    private final S3Uploader uploader;
    private final S3Remover remover;
    private final S3Reader reader;

    public S3FileService(S3Client s3Client, String bucket) {
        Bucket bucketWrapper = Bucket.of(bucket);

        uploader = new S3Uploader(s3Client, bucketWrapper);
        remover = new S3Remover(s3Client, bucketWrapper);
        reader = new S3Reader(s3Client, bucketWrapper);
    }

    /**
     * 파일 업로드
     *
     * @param objectKey  객체 키 (경로 포함)
     * @param file       업로드할 파일
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     * @throws IllegalArgumentException 매개변수가 null이거나 빈 문자열인 경우
     */
    @Override
    public S3UploadResult upload(String objectKey, File file) {
        validateFile(file);

        ObjectKey parsedObjectKey = ObjectKey.of(objectKey);

        S3UploadResponse response = uploader.upload(parsedObjectKey, file);

        if (!response.isSuccess()) {
            return S3UploadResult.fail(response);
        }

        String url = reader.getUrl(parsedObjectKey);

        return S3UploadResult.success(response, url);
    }

    /**
     * 파일 업로드
     *
     * @param objectKey   객체 키 (경로 포함)
     * @param file        업로드할 파일
     * @param contentType 컨텐츠 타입 (MIME 타입)
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     * @throws IllegalArgumentException 매개변수가 null이거나 빈 문자열인 경우
     */
    @Override
    public S3UploadResult upload(String objectKey, File file, String contentType) {
        validateFile(file);

        ObjectKey parsedObjectKey = ObjectKey.of(objectKey);

        S3UploadResponse response = uploader.upload(parsedObjectKey, file, ContentType.of(contentType));

        if (!response.isSuccess()) {
            return S3UploadResult.fail(response);
        }

        String url = reader.getUrl(parsedObjectKey);

        return S3UploadResult.success(response, url);
    }

    /**
     * 파일 업로드
     *
     * @param objectKey     객체 키 (경로 포함)
     * @param inputStream   업로드할 입력 스트림
     * @param contentType   컨텐츠 타입 (MIME 타입)
     * @param contentLength 컨텐츠 길이 (바이트)
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     * @throws IllegalArgumentException 매개변수가 null이거나 빈 문자열인 경우
     */
    @Override
    public S3UploadResult upload(String objectKey, InputStream inputStream, String contentType, long contentLength) {
        validateInputStream(inputStream);

        ObjectKey parsedObjectKey = ObjectKey.of(objectKey);

        S3UploadResponse response = uploader.upload(parsedObjectKey, inputStream, ContentType.of(contentType), ContentLength.of(contentLength));

        if (!response.isSuccess()) {
            return S3UploadResult.fail(response);
        }

        String url = reader.getUrl(parsedObjectKey);

        return S3UploadResult.success(response, url);

    }

    @Override
    public boolean delete(String objectKey) {
        return remover.delete(ObjectKey.of(objectKey));
    }

    private void validateFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new InvalidFileException("File must exist and be a valid file");
        }

        if (file.length() <= 0) {
            throw new InvalidFileException("File size must be greater than 0");
        }
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream must not be null or empty");
        }
    }

}
