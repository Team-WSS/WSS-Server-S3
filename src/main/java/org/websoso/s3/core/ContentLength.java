package org.websoso.s3.core;

import java.util.Objects;

final class ContentLength {

    private final long value;

    private ContentLength(long value) {
        this.value = validateContentLength(value);
    }

    public static ContentLength of(long value) {
        return new ContentLength(value);
    }

    public long getValue() {
        return value;
    }

    private long validateContentLength(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Content length must be greater than 0");
        }

        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentLength that = (ContentLength) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "ContentLength = " + value;
    }

}