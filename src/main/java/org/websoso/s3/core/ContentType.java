package org.websoso.s3.core;

import java.util.Objects;
import org.websoso.s3.exception.validation.InvalidContentTypeException;

final class ContentType {

    private final String value;

    private ContentType(String value) {
        this.value = validateContentType(value).toLowerCase();
    }

    public static ContentType of(String value) {
        return new ContentType(value);
    }

    public ContentType requireImage() {
        if (!isImage()) {
            throw new InvalidContentTypeException("Only image content types are supported");
        }

        return this;
    }

    public String getValue() {
        return value;
    }

    public boolean isImage() {
        return ImageType.getAllowedMimeTypes().stream()
                .anyMatch(type -> type.equals(value));
    }

    private String validateContentType(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidContentTypeException("Content type must not be null or empty");
        }

        // MIME 타입 패턴 검증 (type/subtype 형식)
        if (!value.matches("^[a-zA-Z0-9-]+/[a-zA-Z0-9.-]+$")) {
            throw new InvalidContentTypeException("Invalid content type format: " + value);
        }

        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentType that = (ContentType) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "contentType='" + value;
    }

}
