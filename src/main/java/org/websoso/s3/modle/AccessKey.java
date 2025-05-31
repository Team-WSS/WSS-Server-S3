package org.websoso.s3.modle;

import java.util.Objects;
import org.websoso.s3.exception.InvalidAccessKeyException;

public final class AccessKey {

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
        return value;
    }

    private void requireNonEmpty(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidAccessKeyException("Access key must not be null or empty.");
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
