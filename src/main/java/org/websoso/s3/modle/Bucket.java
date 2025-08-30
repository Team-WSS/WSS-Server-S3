package org.websoso.s3.modle;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import org.websoso.s3.exception.validation.InvalidBucketNameException;

public final class Bucket {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 63;

    private static final Pattern VALID_CHARS_PATTERN = Pattern.compile("^[a-z0-9.-]+$");
    private static final Pattern START_END_PATTERN = Pattern.compile("^[a-z0-9](.*[a-z0-9])?$");
    private static final Pattern CONSECUTIVE_DOTS_PATTERN = Pattern.compile("\\.\\.+");
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
            "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );

    private static final String[] FORBIDDEN_PREFIXES = {
            "xn--",
            "sthree-",
            "amzn-s3-demo-"
    };

    private static final String[] FORBIDDEN_SUFFIXES = {
            "-s3alias",
            "--ol-s3",
            ".mrap",
            "--x-s3",
            "--table-s3"
    };

    private final String value;

    private Bucket(String value) {
        this.value = validateBucket(value);
    }

    public static Bucket of(String value) {
        return new Bucket(value);
    }

    public String getValue() {
        return value;
    }

    private String validateBucket(String value) {
        requireNonEmpty(value);
        validateLength(value);
        validateCharacters(value);
        validateStartEnd(value);
        checkForbiddenPatterns(value);
        checkForbiddenPrefix(value);
        checkForbiddenSuffix(value);
        return value;
    }

    private void requireNonEmpty(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidBucketNameException("Bucket name must not be null or empty");
        }
    }

    private void validateLength(String value) {
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new InvalidBucketNameException(
                    String.format("Bucket name must be between %d and %d characters. Actual length: %d",
                            MIN_LENGTH, MAX_LENGTH, value.length()));
        }
    }

    private void validateCharacters(String value) {
        if (!VALID_CHARS_PATTERN.matcher(value).matches()) {
            throw new InvalidBucketNameException(
                    "Bucket name can only contain lowercase letters, numbers, hyphens (-), and dots (.)");
        }
    }

    private void validateStartEnd(String value) {
        if (!START_END_PATTERN.matcher(value).matches()) {
            throw new InvalidBucketNameException("Bucket name must start and end with a letter or number");
        }

        if (value.startsWith("-") || value.endsWith("-")) {
            throw new InvalidBucketNameException("Bucket name cannot start or end with a hyphen (-)");
        }

        if (value.startsWith(".") || value.endsWith(".")) {
            throw new InvalidBucketNameException("Bucket name cannot start or end with a dot (.)");
        }
    }

    private void checkForbiddenPatterns(String value) {
        if (CONSECUTIVE_DOTS_PATTERN.matcher(value).find()) {
            throw new InvalidBucketNameException("Bucket name cannot contain consecutive dots (..)");
        }
        if (IP_ADDRESS_PATTERN.matcher(value).matches()) {
            throw new InvalidBucketNameException("Bucket name cannot be formatted as an IP address");
        }
    }

    private void checkForbiddenPrefix(String value) {
        Arrays.stream(FORBIDDEN_PREFIXES)
                .filter(value::startsWith)
                .findFirst()
                .ifPresent(prefix -> {
                    throw new InvalidBucketNameException(
                            String.format("Bucket name cannot start with the reserved prefix '%s'", prefix));
                });
    }

    private void checkForbiddenSuffix(String value) {
        Arrays.stream(FORBIDDEN_SUFFIXES)
                .filter(value::endsWith)
                .findFirst()
                .ifPresent(suffix -> {
                    throw new InvalidBucketNameException(
                            String.format("Bucket name cannot end with the reserved suffix '%s'", suffix));
                });
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bucket bucket = (Bucket) o;
        return Objects.equals(value, bucket.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "Bucket = " + value;
    }

}
