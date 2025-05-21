package org.websoso.s3.core.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class FastMimeTypeDetectionStrategyTest {

    private final FastMimeTypeDetectionStrategy strategy = new FastMimeTypeDetectionStrategy();

    @DisplayName("파일에서 MIME 타입을 정상 감지한다")
    @Test
    void detectFile_success() throws IOException {
        File file = new File("src/test/resources/test.png");

        String mimeType = strategy.detect(file);

        assertThat(mimeType).startsWith("image/");
    }

    @DisplayName("mark/reset 가능한 InputStream에서 MIME 타입을 감지한다")
    @Test
    void detectInputStream_success() throws IOException {
        File file = new File("src/test/resources/test.png");

        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            String mimeType = strategy.detect(input);
            assertThat(mimeType).startsWith("image/");
        }
    }

    @DisplayName("mark/reset 불가능한 InputStream은 예외를 던진다")
    @Test
    void detectInputStream_markNotSupported_throws() {
        InputStream nonMarkable = new ByteArrayInputStream(new byte[0]) {
            @Override public boolean markSupported() { return false; }
        };

        assertThatThrownBy(() -> strategy.detect(nonMarkable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mark/reset");
    }
}