package org.websoso.s3.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.websoso.s3.exception.InvalidObjectKeyException;

class ObjectKeyTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "simple-file.txt",
            "folder/file.txt",
            "path/to/file.json",
            "file_with_underscore",
            "file-with-dash",
            "file.with.dots",
            "file'with'quotes",
            "file(with)parentheses",
            "file*with*asterisk",
            "123456789",
            "path/with/numbers123",
            "a",
            "A",
            "file!exclamation"
    })
    @DisplayName("Key 객체를 생성한다.")
    void createValidKey(String validKey) {
        // When
        ObjectKey objectKey = ObjectKey.of(validKey);

        // Then
        assertThat(objectKey.getValue()).isEqualTo(validKey);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("null이거나 빈 문자열로 Key 객체를 생성할 수 없다.")
    void createKeyWithNullOrEmpty(String invalidKey) {
        // When & Then
        assertThatThrownBy(() -> ObjectKey.of(invalidKey))
                .isInstanceOf(InvalidObjectKeyException.class)
                .hasMessage("Object key must not be null or empty");
    }

    @Test
    @DisplayName("1024 바이트의 문자열로 Key 객체를 생성할 수 있다.")
    void createKeyWithMaxLengthValue() {
        // Given
        String maxLengthKey = "a".repeat(1024);

        // When
        ObjectKey objectKey = ObjectKey.of(maxLengthKey);

        // Then
        assertThat(objectKey.getValue()).isEqualTo(maxLengthKey);
    }

    @Test
    @DisplayName("1024 바이트를 초과하는 문자열로 Key 객체를 생성할 수 없다.")
    void createKeyWithTooLongValue() {
        // Given
        String tooLongKey = "a".repeat(1025);

        // When & Then
        assertThatThrownBy(() -> ObjectKey.of(tooLongKey))
                .isInstanceOf(InvalidObjectKeyException.class)
                .hasMessage("Object key is too long");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "file with space",
            "file@with@at",
            "file#with#hash",
            "file$with$dollar",
            "file%with%percent",
            "file^with^caret",
            "file&with&ampersand",
            "file+with+plus",
            "file=with=equals",
            "file[with]brackets",
            "file{with}braces",
            "file|with|pipe",
            "file\\with\\backslash",
            "file:with:colon",
            "file;with;semicolon",
            "file\"with\"quotes",
            "file<with>angles",
            "file,with,comma",
            "file?with?question"
    })
    @DisplayName("유효하지 않은 문자로 구성된 문자열로 Key 객체를 생성할 수 없다.")
    void createKeyWithInvalidCharacters(String invalidKey) {
        // When & Then
        assertThatThrownBy(() -> ObjectKey.of(invalidKey))
                .isInstanceOf(InvalidObjectKeyException.class)
                .hasMessage("Object key must contain valid relative path");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "folder/file.txt",
            "path/to/deep/file.txt",
            "a/b/c/d/e/f",
            "./file.txt",
            "folder/./file.txt",
            "folder/../folder/file.txt",
            "a/../a/file.txt"
    })
    @DisplayName("유효한 상대 경로 Key 객체를 생성한다.")
    void createKeyWithValidRelativePath(String validPath) {
        // When
        ObjectKey objectKey = ObjectKey.of(validPath);

        // Then
        assertThat(objectKey.getValue()).isEqualTo(validPath);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "../file.txt",
            "folder/../../file.txt",
            "../../../file.txt",
            "a/../../../b/file.txt",
            "folder/../../../another/file.txt"
    })
    @DisplayName("상위 디렉토리로 벗어나는 무효한 상대 경로로 Key 객체를 생성할 수 없다.")
    void createKeyWithInvalidRelativePath(String invalidPath) {
        // When & Then
        assertThatThrownBy(() -> ObjectKey.of(invalidPath))
                .isInstanceOf(InvalidObjectKeyException.class)
                .hasMessage("Invalid relative path");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "file//with//double//slash",
            "path///triple/slash"
    })
    @DisplayName("빈 세그먼트를 포함해도 유효한 문자로 구성된 경로는 Key 생성에 성공한다")
    void createKeyWhenPathContainsEmptySegmentsButIsValid(String value) {

        // When
        ObjectKey objectKey = ObjectKey.of(value);

        // Then
        assertThat(objectKey.getValue()).isEqualTo(value);
    }


    @Test
    @DisplayName("한글로 Key객체를 생성할 수 없다.")
    void throwExceptionWhenContainsKoreanCharacters() {
        // Given
        String koreanKey = "한글파일명.txt";

        // When & Then
        assertThatThrownBy(() -> ObjectKey.of(koreanKey))
                .isInstanceOf(InvalidObjectKeyException.class)
                .hasMessage("Object key must contain valid relative path");
    }

    @Test
    @DisplayName("절대 경로가 포함된 경우, Key 객체를 생성할 수 없다.")
    void absolutePathThrowsException() {
        // Given
        String absolutePath = "/absolute/path";

        // When & Then
        assertThatThrownBy(() -> ObjectKey.of(absolutePath))
                .isInstanceOf(InvalidObjectKeyException.class)
                .hasMessage("Object key must be a relative path, not absolute");
    }

    @Test
    @DisplayName("파일명이 누락된 경우, Key 객체를 생성할 수 없다.")
    void endsWithSlashThrowsException() {
        // Given
        String absolutePath = "path/";

        // When & Then
        assertThatThrownBy(() -> ObjectKey.of(absolutePath))
                .isInstanceOf(InvalidObjectKeyException.class)
                .hasMessage("Object key must not end with a slash, file name is missing");
    }
}