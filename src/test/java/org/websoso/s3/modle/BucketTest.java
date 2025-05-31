package org.websoso.s3.modle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.websoso.s3.exception.InvalidBucketNameException;

class BucketTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "my-bucket",
            "bucket123",
            "bucket.name.with.dots",
            "abc",
            "bucket0"
    })
    @DisplayName("Bucket 객체를 생성한다.")
    void createValidBucket(String value) {
        // When
        Bucket bucket = Bucket.of(value);

        // Then
        assertThat(bucket.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("null 값으로 Bucket 객체를 생성할 수 없다.")
    void nullValueThrowsException() {
        // Given
        String value = null;

        // When & Then
        assertThatThrownBy(() -> Bucket.of(value))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessage("Bucket name must not be null or empty");
    }

    @Test
    @DisplayName("빈 문자열로 Bucket 객체를 생성할 수 없다.")
    void blankValueThrowsException() {
        // Given
        String value = " ";

        // When & Then
        assertThatThrownBy(() -> Bucket.of(value))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessage("Bucket name must not be null or empty");
    }

    @Test
    @DisplayName("버킷 이름 길이가 3 미만이거나 63 초과인 경우 예외가 발생한다.")
    void invalidLengthThrowsException() {
        // Given
        String tooShort = "ab";
        String tooLong = "a".repeat(64);

        // When & Then
        assertThatThrownBy(() -> Bucket.of(tooShort))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessageContaining("Bucket name must be between 3 and 63 characters");

        assertThatThrownBy(() -> Bucket.of(tooLong))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessageContaining("Bucket name must be between 3 and 63 characters");
    }

    @Test
    @DisplayName("소문자, 숫자, 하이픈, 점 이외의 문자가 포함되면 예외가 발생한다.")
    void invalidCharactersThrowsException() {
        // Given
        String upperCase = "BucketName";
        String underscore = "bucket_name";
        String specialChar = "bucket@name";

        // When & Then
        assertThatThrownBy(() -> Bucket.of(upperCase))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessage("Bucket name can only contain lowercase letters, numbers, hyphens (-), and dots (.)");

        assertThatThrownBy(() -> Bucket.of(underscore))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessage("Bucket name can only contain lowercase letters, numbers, hyphens (-), and dots (.)");

        assertThatThrownBy(() -> Bucket.of(specialChar))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessage("Bucket name can only contain lowercase letters, numbers, hyphens (-), and dots (.)");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-bucket",
            "bucket-",
            ".bucket",
            "bucket."
    })
    @DisplayName("버킷 이름이 하이픈 또는 점으로 시작하거나 끝나면 예외가 발생한다.")
    void invalidStartOrEndThrowsException(String value) {
        // When & Then
        assertThatThrownBy(() -> Bucket.of(value))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessage("Bucket name must start and end with a letter or number");
    }

    @Test
    @DisplayName("연속된 점(..)이 포함된 버킷 이름은 생성할 수 없다.")
    void consecutiveDotsThrowsException() {
        // Given
        String value = "bucket..name";

        // When & Then
        assertThatThrownBy(() -> Bucket.of(value))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessage("Bucket name cannot contain consecutive dots (..)");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "192.168.0.1",
            "255.255.255.255"
    })
    @DisplayName("IP 주소 형식의 버킷 이름은 생성할 수 없다.")
    void ipAddressFormatThrowsException(String value) {
        // When & Then
        assertThatThrownBy(() -> Bucket.of(value))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessage("Bucket name cannot be formatted as an IP address");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "xn--bucket",
            "sthree-bucket",
            "amzn-s3-demo-bucket"
    })
    @DisplayName("금지된 접두사로 시작하는 버킷 이름은 생성할 수 없다.")
    void forbiddenPrefixThrowsException(String value) {
        // When & Then
        assertThatThrownBy(() -> Bucket.of(value))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessageContaining("Bucket name cannot start with the reserved prefix");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "bucket-s3alias",
            "bucket--ol-s3",
            "bucket.mrap",
            "bucket--x-s3",
            "bucket--table-s3"
    })
    @DisplayName("금지된 접미사로 끝나는 버킷 이름은 생성할 수 없다.")
    void forbiddenSuffixThrowsException(String value) {
        // When & Then
        assertThatThrownBy(() -> Bucket.of(value))
                .isInstanceOf(InvalidBucketNameException.class)
                .hasMessageContaining("Bucket name cannot end with the reserved suffix");

    }
}