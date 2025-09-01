package org.websoso.s3.modle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.websoso.s3.exception.validation.InvalidAccessKeyException;

class AccessKeyTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "AKIA12345678901234",
            "ASIAABCDEFGHIJKLMNOPQRST",
            "AKIA00000000000000",
            "ASIA99999999999999999999"
    })
    @DisplayName("AccessKey 객체를 생성한다.")
    void createValidAccessKey(String value) {
        // When
        AccessKey accessKey = AccessKey.of(value);

        // Then
        assertThat(accessKey.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("null 값으로 AccessKey 객체를 생성할 수 없다.")
    void nullValueThrowsException() {
        // Given
        String value = null;

        // When & Then
        assertThatThrownBy(() -> AccessKey.of(value))
                .isInstanceOf(InvalidAccessKeyException.class)
                .hasMessage("Access key must not be null or empty.");
    }

    @Test
    @DisplayName("빈 문자열로 AccessKey 객체를 생성할 수 없다.")
    void blankValueThrowsException() {
        // Given
        String value = "  ";

        // When & Then
        assertThatThrownBy(() -> AccessKey.of(value))
                .isInstanceOf(InvalidAccessKeyException.class)
                .hasMessage("Access key must not be null or empty.");
    }

    @Test
    @DisplayName("길이가 16자 미만이거나 128자를 초과하는 AccessKey는 생성할 수 없다.")
    void invalidLengthThrowsException() {
        String toShort = "AKIA1234567890";
        String toLong = "AKIA12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345";

        // When & Then
        assertThatThrownBy(() -> AccessKey.of(toShort))
                .isInstanceOf(InvalidAccessKeyException.class)
                .hasMessage("Access key length must be between 16 and 128 characters.");

        assertThatThrownBy(() -> AccessKey.of(toLong))
                .isInstanceOf(InvalidAccessKeyException.class)
                .hasMessage("Access key length must be between 16 and 128 characters.");
    }

    @Test
    @DisplayName("유효하지 않은 형식의 AccessKey는 생성할 수 없다.")
    void invalidFormatThrowsException() {
        // Given
        String keyWithLowerCaseInside = "AKIA1234abcd5678";
        String keyStartingWithLowerCase = "akia123456789012";
        String specialCharKey = "ASIA1234!@#$5678";

        // When & Then
        assertThatThrownBy(() -> AccessKey.of(keyWithLowerCaseInside))
                .isInstanceOf(InvalidAccessKeyException.class)
                .hasMessage("Access key must contain only uppercase letters and numbers.");

        assertThatThrownBy(() -> AccessKey.of(keyStartingWithLowerCase))
                .isInstanceOf(InvalidAccessKeyException.class)
                .hasMessage("Access key must contain only uppercase letters and numbers.");

        assertThatThrownBy(() -> AccessKey.of(specialCharKey))
                .isInstanceOf(InvalidAccessKeyException.class)
                .hasMessage("Access key must contain only uppercase letters and numbers.");
    }

    @Test
    @DisplayName("AccessKey는 'AKIA' 또는 'ASIA'로 시작하지 않으면 생성할 수 없다.")
    void invalidPrefixThrowsException() {
        // Given
        String keyStartingWithInvalidPrefix1 = "BKIA123456789012";

        // When & Then
        assertThatThrownBy(() -> AccessKey.of(keyStartingWithInvalidPrefix1))
                .isInstanceOf(InvalidAccessKeyException.class)
                .hasMessage("Access key must start with 'AKIA' or 'ASIA'.");
    }
}