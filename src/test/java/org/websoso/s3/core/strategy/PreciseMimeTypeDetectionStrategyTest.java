package org.websoso.s3.core.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PreciseMimeTypeDetectionStrategyTest {

    private final PreciseMimeTypeDetectionStrategy strategy = new PreciseMimeTypeDetectionStrategy();

    @DisplayName("파일에서 MIME 타입을 정밀하게 감지한다")
    @Test
    void detectFile_success() throws IOException {
        File file = new File("src/test/resources/test.png");

        String mimeType = strategy.detect(file);

        assertThat(mimeType).startsWith("image/");
    }

    @DisplayName("InputStream에서 MIME 타입을 정밀하게 감지한다")
    @Test
    void detectInputStream_success() throws IOException {
        File file = new File("src/test/resources/test.png");

        try (InputStream input = new FileInputStream(file)) {
            String mimeType = strategy.detect(input);
            assertThat(mimeType).startsWith("image/");
        }
    }

    @DisplayName("손상된 InputStream은 IOException을 던진다")
    @Test
    void detectInputStream_error_shouldThrowIOException() {
        InputStream broken = new InputStream() {
            @Override public int read() { return -1; }
        };

        assertThatThrownBy(() -> strategy.detect(broken))
                .isInstanceOf(IOException.class);
    }
}
