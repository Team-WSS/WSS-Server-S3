package org.websoso.s3.core.strategy;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 빠른 방식으로 MIME 타입을 감지하는 전략 구현체입니다.
 * <p>
 * 주로 파일 확장자와 파일 헤더의 일부만 읽어 감지하며, 속도가 빠른 대신 정확도는 떨어질 수 있습니다.
 * </p>
 */
public class FastMimeTypeDetectionStrategy implements MimeTypeDetectionStrategy {

    private static final int MARK_LIMIT = 2048;
    private static final Tika tika = new Tika();

    @Override
    public String detect(InputStream inputStream) throws IOException {
        if (!inputStream.markSupported()) {
            throw new IllegalArgumentException("InputStream must support mark/reset");
        }

        inputStream.mark(MARK_LIMIT);
        try {
            return tika.detect(inputStream);
        } finally {
            try {
                inputStream.reset();
            } catch (IOException e) {
                throw new IOException("MIME detection succeeded, but failed to reset InputStream for reuse", e);
            }
        }
    }

    @Override
    public String detect(File file) throws IOException {
        return tika.detect(file);
    }
}