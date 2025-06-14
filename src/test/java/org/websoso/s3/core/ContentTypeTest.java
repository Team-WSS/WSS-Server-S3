package org.websoso.s3.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.websoso.s3.exception.InvalidContentTypeException;

class ContentTypeTest {

    @Test
    @DisplayName("ContentType 객체를 생성")
    void createValidContentType() {
        // Given
        String value = "application/json";

        // When
        ContentType contentType = ContentType.of(value);

        // Then
        assertThat(contentType.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("대문자인 경우 소문자로 변환된다.")
    void contentTypeStoredAsLowerCase() {
        // Given
        String value = "IMAGE/JPEG";

        // When
        ContentType contentType = ContentType.of(value);

        // Then
        assertThat(contentType.getValue()).isNotEqualTo(value);
        assertThat(contentType.getValue()).isEqualTo(value.toLowerCase());
    }

    @Test
    @DisplayName("null 값으로 ContentType 객체를 생성할 수 없다.")
    void nullValueThrowsException() {
        // Given
        String value = null;

        // When & Then
        assertThatThrownBy(()-> ContentType.of(value))
                .isInstanceOf(InvalidContentTypeException.class)
                .hasMessage("Content type must not be null or empty");
    }

    @Test
    @DisplayName("공백 문자열로 ContentType 객체를 생성할 수 없다.")
    void blankValueThrowsException() {
        // Given
        String value = "";

        // When & Then
        assertThatThrownBy(()-> ContentType.of(value))
                .isInstanceOf(InvalidContentTypeException.class)
                .hasMessage("Content type must not be null or empty");
    }

    @ParameterizedTest
    @DisplayName("MIME 타입 패턴을 따르지 않은 문자열로 ContentType 객체를 생성할 수 없다.")
    @ValueSource(strings = {"image", "image_png", "image\\jpeg", "image@"})
    void invalidFormatThrowsException(String value) {
        // When & Then
        assertThatThrownBy(()-> ContentType.of(value))
                .isInstanceOf(InvalidContentTypeException.class)
                .hasMessage("Invalid content type format: " + value);
    }

    @Test
    @DisplayName("ContentType 객체가 이미지 타입인지 알 수 있다.")
    void isImageReturnsTrue() {
        // Given
        String imageValue = "image/png";
        String nonImageValue = "application/json";

        // When
        ContentType imageContentType = ContentType.of(imageValue);
        ContentType nonImageContentType = ContentType.of(nonImageValue);

        // Then
        assertThat(imageContentType.isImage()).isTrue();
        assertThat(nonImageContentType.isImage()).isFalse();
    }

    @Test
    @DisplayName("이미지 ContentType 객체를 생성한다.")
    void imageOnlyOfSucceedsOnImage() {
        // Given
        String value = "image/png";

        // When
        ContentType contentType = ContentType.of(value).requireImage();

        // Then
        assertThat(contentType.getValue()).isEqualTo(value);
        assertThat(contentType.isImage()).isTrue();
    }

    @Test
    @DisplayName("이미지 타입이 아닌 문자열로 이미지 ContentType 객체를 생성할 수 없다.")
    void imageOnlyOfThrowsOnNonImage() {
        // Given
        String value = "application/json";

        // When & Then
        assertThatThrownBy(() -> ContentType.of(value).requireImage())
                .isInstanceOf(InvalidContentTypeException.class)
                .hasMessage("Only image content types are supported");
    }
}
