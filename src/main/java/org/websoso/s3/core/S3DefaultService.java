package org.websoso.s3.core;

import org.websoso.s3.modle.S3UploadResult;

import java.io.File;
import java.io.InputStream;

/**
 * S3 객체 업로드 및 삭제를 위한 인터페이스입니다.
 * <p>
 * 다양한 타입의 파일 입력(File, InputStream 등)을 지원하며,
 * 업로드 결과는 {@link S3UploadResult}로 반환됩니다.
 * </p>
 */
public interface S3DefaultService {

    /**
     * File 객체를 S3에 업로드
     *
     * @param key  객체 키 (경로 포함)
     * @param file 업로드할 파일
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     */
    S3UploadResult upload(String key, File file);

    /**
     * File 객체를 S3에 업로드
     *
     * @param key         객체 키 (경로 포함)
     * @param file        업로드할 파일
     * @param contentType 컨텐츠 타입 (MIME 타입, 예: image/jpeg)
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     */
    S3UploadResult upload(String key, File file, String contentType);

    /**
     * InputStream 객체를 S3에 업로드
     *
     * @param key           객체 키 (경로 포함)
     * @param inputStream   업로드할 입력 스트림
     * @param contentType   컨텐츠 타입 (MIME 타입, 예: image/jpeg)
     * @param contentLength 컨텐츠 길이 (바이트 단위)
     * @return 업로드 결과를 담은 {@link S3UploadResult} 객체
     */
    S3UploadResult upload(String key, InputStream inputStream, String contentType, long contentLength);

    /**
     * S3에 존재하는 객체를 삭제
     *
     * @param key 객체 키 (경로 포함)
     * @return 삭제 성공 여부
     */
    boolean delete(String key);

}
