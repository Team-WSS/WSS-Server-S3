package org.websoso.s3.modle;

import java.util.Objects;
import org.websoso.s3.exception.InvalidAccessKeyException;

public final class AccessKey {

    private static final int MIN_LENGTH = 16;
    private static final int MAX_LENGTH = 128;

    private static final String PERMANENT = "AKIA";
    private static final String TEMPORARY = "ASIA";

    private static final String UPPERCASE_ALPHANUMERIC_REGEX = "^[A-Z0-9]+$";

    private final String value;

    private AccessKey(String value) {
        this.value = validateAccessKey(value);
    }

    public static AccessKey of(String value) {
        return new AccessKey(value);
    }

    public String getValue() {
        return value;
    }

    private String validateAccessKey(String value) {
        requireNonEmpty(value);
        validateLength(value);
        validateFormat(value);
        return value;
    }

    private void requireNonEmpty(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidAccessKeyException("Access key must not be null or empty.");
        }
    }

    private void validateLength(String value) {
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new InvalidAccessKeyException("Access key length must be between 16 and 128 characters.");
        }
    }

    private void validateFormat(String value) {
        if (!value.matches(UPPERCASE_ALPHANUMERIC_REGEX)) {
            throw new InvalidAccessKeyException("Access key must contain only uppercase letters and numbers.");
        }

        // AWS Access Key ID는 'AKIA'(일반 사용자) 또는 'ASIA'(임시 자격증명)로 시작함
        if (!value.startsWith(PERMANENT) && !value.startsWith(TEMPORARY)) {
            throw new InvalidAccessKeyException("Access key must start with 'AKIA' or 'ASIA'.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccessKey accessKey = (AccessKey) o;
        return Objects.equals(value, accessKey.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "AccessKey = " + value;
    }
}