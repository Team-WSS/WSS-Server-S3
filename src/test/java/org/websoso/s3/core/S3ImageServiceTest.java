package org.websoso.s3.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.websoso.s3.core.strategy.PreciseMimeTypeDetectionStrategy;
import org.websoso.s3.core.strategy.FastMimeTypeDetectionStrategy;
import org.websoso.s3.exception.InvalidImageException;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;


class S3ImageServiceTest {

    private S3ImageService imageService;

    @BeforeEach
    void setUp() {
        S3Client s3Client = mock(S3Client.class);
        String bucket = "test-bucket";

        imageService = new S3ImageService(s3Client, bucket, new FastMimeTypeDetectionStrategy());
    }

    @DisplayName("지원하지 않는 확장자일 경우 예외를 던진다")
    @Test
    void uploadFile_invalidExtension_throwsException() {
        // given
        File file = new File("src/test/resources/test.txt");

        // when & then
        assertThatThrownBy(() -> imageService.upload("invalid/test.txt", file))
                .isInstanceOf(InvalidImageException.class);
    }

    @DisplayName("contentType이 허용되지 않으면 예외를 던진다")
    @Test
    void upload_invalidMimeType_throwsException() {
        // given
        File file = new File("src/test/resources/test.png");
        String key = "images/test.png";
        String invalidContentType = "application/pdf";

        // when & then
        assertThatThrownBy(() -> imageService.upload(key, file, invalidContentType))
                .isInstanceOf(InvalidImageException.class);
    }

    @DisplayName("확장자는 jpg이더라도, 실제 MimeType이 지원되지 않는 타입이면 예외를 던진다")
    @Test
    void upload_fakeJpgFile_shouldThrowMimeTypeException() {
        // given
        File fakeJpgFile = new File("src/test/resources/fake-image.jpg"); // 내용은 텍스트, 확장자만 .jpg

        // when & then
        assertThatThrownBy(() -> imageService.upload("images/fake.jpg", fakeJpgFile))
                .isInstanceOf(InvalidImageException.class);
    }

    @DisplayName("null InputStream 업로드 시 예외를 던진다")
    @Test
    void upload_nullInputStream_throwsException() {
        // when & then
        assertThatThrownBy(() ->
                imageService.upload("key", null, "image/jpeg", 1000))
                .isInstanceOf(IllegalArgumentException.class);
    }
}