package org.websoso.s3.core;

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
        uploader = new S3Uploader(s3Client, bucket);
        remover = new S3Remover(s3Client, bucket);
        reader = new S3Reader(s3Client, bucket);
    }

    /**
     * 파일 업로드
     *
     * @param key  객체 키 (경로 포함)
     * @param file 업로드할 파일
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     * @throws IllegalArgumentException 매개변수가 null이거나 빈 문자열인 경우
     */
    @Override
    public S3UploadResult upload(String key, File file) {

        validateKey(key);
        validateFile(file);

        S3UploadResponse response = uploader.upload(key, file);

        if (!response.isSuccess()) {
            S3UploadResult.fail(response);
        }

        String url = reader.getUrl(key);

        return S3UploadResult.success(response, url);
    }

    /**
     * 파일 업로드
     *
     * @param key         객체 키 (경로 포함)
     * @param file        업로드할 파일
     * @param contentType 컨텐츠 타입 (MIME 타입)
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     * @throws IllegalArgumentException 매개변수가 null이거나 빈 문자열인 경우
     */
    @Override
    public S3UploadResult upload(String key, File file, String contentType) {

        validateKey(key);
        validateFile(file);
        validateContentType(contentType);

        S3UploadResponse response = uploader.upload(key, file, contentType);

        if (!response.isSuccess()) {
            S3UploadResult.fail(response);
        }

        String url = reader.getUrl(key);

        return S3UploadResult.success(response, url);
    }

    /**
     * 파일 업로드
     *
     * @param key           객체 키 (경로 포함)
     * @param inputStream   업로드할 입력 스트림
     * @param contentType   컨텐츠 타입 (MIME 타입)
     * @param contentLength 컨텐츠 길이 (바이트)
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     * @throws IllegalArgumentException 매개변수가 null이거나 빈 문자열인 경우
     */
    @Override
    public S3UploadResult upload(String key, InputStream inputStream, String contentType, long contentLength) {

        validateKey(key);
        validateInputStream(inputStream);
        validateContentType(contentType);
        validateContentLength(contentLength);

        S3UploadResponse response = uploader.upload(key, inputStream, contentType, contentLength);

        if (!response.isSuccess()) {
            S3UploadResult.fail(response);
        }

        String url = reader.getUrl(key);

        return S3UploadResult.success(response, url);

    }

    @Override
    public boolean delete(String key) {
        validateKey(key);

        return remover.delete(key);
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Object key must not be null or empty");
        }
    }

    private void validateFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File must exist and be a valid file");
        }

        if (file.length() <= 0) {
            throw new IllegalArgumentException("File size must be greater than 0");
        }
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream must not be null or empty");
        }
    }

    private void validateContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content type must not be null or empty");
        }
    }

    private void validateContentLength(long contentLength) {
        if (contentLength <= 0) {
            throw new IllegalArgumentException("Content length must be greater than 0");
        }
    }

}
