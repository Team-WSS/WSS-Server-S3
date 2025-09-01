package org.websoso.s3.modle;

import java.util.Objects;
import org.websoso.s3.exception.validation.InvalidSecretKeyException;

public final class SecretKey {

    private final String value;

    private SecretKey(String value) {
        this.value = validateSecretKey(value);
    }

    public static SecretKey of(String value) {
        return new SecretKey(value);
    }

    public String getValue() {
        return value;
    }

    private String validateSecretKey(String value) {
        requireNonEmpty(value);
        return value;
    }

    private void requireNonEmpty(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidSecretKeyException("Secret key must not be null or empty.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SecretKey secretKey = (SecretKey) o;
        return Objects.equals(value, secretKey.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "SecretKey = " + value;
    }

}
