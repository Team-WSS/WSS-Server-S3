package org.websoso.s3.core.strategy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * MIME 타입을 감지하기 위한 전략 인터페이스입니다.
 * <p>
 * 구현체는 빠른 감지(확장자 기반) 또는 정밀 감지(Tika 파서 기반) 방식으로 분기됩니다.
 * </p>
 */
public interface MimeTypeDetectionStrategy {

    /**
     * 입력 스트림으로부터 MIME 타입을 감지합니다.
     *
     * @param inputStream 분석할 InputStream
     * @return 감지된 MIME 타입 문자열
     * @throws IOException 감지 중 I/O 오류 발생 시
     */
    String detect(InputStream inputStream) throws IOException;

    /**
     * 파일 객체로부터 MIME 타입을 감지합니다.
     *
     * @param file 분석할 파일
     * @return 감지된 MIME 타입 문자열
     * @throws IOException 감지 중 I/O 오류 발생 시
     */
    String detect(File file) throws IOException;
}