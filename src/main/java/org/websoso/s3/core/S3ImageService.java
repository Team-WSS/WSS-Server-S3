package org.websoso.s3.core;

import org.websoso.s3.core.strategy.MimeTypeDetectionStrategy;
import org.websoso.s3.exception.InvalidImageException;
import org.websoso.s3.modle.S3UploadResponse;
import org.websoso.s3.modle.S3UploadResult;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * S3 파일 업로드 및 삭제를 위한 S3DefaultService 인터페이스의 구현체 입니다.
 * <p>
 * 타입은 정해진 이미지 타입 {@link #ALLOWED_IMAGE_MIME_TYPES} {@link #ALLOWED_IMAGE_EXTENSIONS} 만을 지원하며,
 * 업로드는 {@link File} 또는 {@link InputStream}을 통한 입력을 지원합니다.
 * 업로드 결과는 {@link S3UploadResult}로 반환됩니다.
 * </p>
 */
public class S3ImageService implements S3DefaultService {

    private final S3Uploader uploader;
    private final S3Remover remover;
    private final S3Reader reader;
    private final MimeTypeDetectionStrategy mimeDetector;
    private static final Set<String> ALLOWED_IMAGE_MIME_TYPES = ImageType.getAllowedMimeTypes();
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = ImageType.getAllowedExtensions();

    public S3ImageService(S3Client s3Client, String bucket, MimeTypeDetectionStrategy mimeDetector) {
        this.uploader = new S3Uploader(s3Client, bucket);
        this.remover = new S3Remover(s3Client, bucket);
        this.reader = new S3Reader(s3Client, bucket);
        this.mimeDetector = mimeDetector;
    }

    /**
     * 이미지 업로드
     *
     * @param key  객체 키 (경로 포함)
     * @param file 업로드할 파일
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     * @throws IllegalArgumentException 매개변수가 null이거나 빈 문자열인 경우, 규정된 이미지 형식을 벗어난 경우
     */
    @Override
    public S3UploadResult upload(String key, File file) {
        validateKey(key);
        validateImage(file);

        S3UploadResponse response = uploader.upload(key, file);

        if (!response.isSuccess()) {
            return S3UploadResult.fail(response);
        }

        String url = reader.getUrl(key);
        return S3UploadResult.success(response, url);
    }

    /**
     * 이미지 업로드
     *
     * @param key         객체 키 (경로 포함)
     * @param file        업로드할 파일
     * @param contentType 컨텐츠 타입 (MIME 타입)
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     * @throws IllegalArgumentException 매개변수가 null이거나 빈 문자열인 경우, 규정된 이미지 형식을 벗어난 경우
     */
    @Override
    public S3UploadResult upload(String key, File file, String contentType) {
        validateKey(key);
        validateImage(file);
        validateContentType(contentType);

        S3UploadResponse response = uploader.upload(key, file, contentType);

        if (!response.isSuccess()) {
            return S3UploadResult.fail(response);
        }

        String url = reader.getUrl(key);
        return S3UploadResult.success(response, url);
    }

    /**
     * 이미지 업로드
     *
     * @param key           객체 키 (경로 포함)
     * @param inputStream   업로드할 입력 스트림
     * @param contentType   컨텐츠 타입 (MIME 타입)
     * @param contentLength 컨텐츠 길이 (바이트)
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     * @throws IllegalArgumentException 매개변수가 null이거나 빈 문자열인 경우, 규정된 이미지 형식을 벗어난 경우
     */
    @Override
    public S3UploadResult upload(String key, InputStream inputStream, String contentType, long contentLength) {
        validateKey(key);
        validateInputStream(inputStream);
        validateImage(inputStream);
        validateContentType(contentType);
        validateContentLength(contentLength);

        S3UploadResponse response = uploader.upload(key, inputStream, contentType, contentLength);

        if (!response.isSuccess()) {
            return S3UploadResult.fail(response);
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

    private void validateImage(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new InvalidImageException("Image File must exist and be a valid file");
        }

        if (file.length() <= 0) {
            throw new InvalidImageException("Image File size must be greater than 0");
        }

        String extension = getFileExtension(file);
        boolean extensionAllowed = ALLOWED_IMAGE_EXTENSIONS.contains(extension);
        if (!extensionAllowed) {
            throw new InvalidImageException("Image File type not allowed: extension " + extension);
        }

        String detectedMimeType;
        try {
            detectedMimeType = mimeDetector.detect(file);
        } catch (IOException e) {
            throw new InvalidImageException("Failed to detect MIME type", e);
        }
        boolean mimeAllowed = ALLOWED_IMAGE_MIME_TYPES.contains(detectedMimeType);
        if (!mimeAllowed) {
            throw new InvalidImageException("Image File type not allowed: MIME type " + detectedMimeType);
        }
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
    }

    private void validateContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new InvalidImageException("Content type must not be null or empty");
        }

        if (!ALLOWED_IMAGE_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidImageException("Image File type not allowed: MIME type " + contentType);
        }
    }

    private void validateContentLength(long contentLength) {
        if (contentLength <= 0) {
            throw new InvalidImageException("Content length must be greater than 0");
        }
    }

    private void validateImage(InputStream inputStream) {
        try {
            String detectedMimeType = mimeDetector.detect(inputStream);

            if (!ALLOWED_IMAGE_MIME_TYPES.contains(detectedMimeType)) {
                throw new InvalidImageException("Image File type not allowed: detected MIME type " + detectedMimeType);
            }
        } catch (IOException e) {
            throw new InvalidImageException("Failed to detect MIME type from InputStream", e);
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            throw new InvalidImageException("Image File has no extension: " + fileName);
        }
        return fileName.substring(index).toLowerCase();
    }
}