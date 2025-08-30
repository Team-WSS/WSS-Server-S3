package org.websoso.s3.core;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;
import org.websoso.s3.exception.InvalidObjectKeyException;

final class ObjectKey {

    private static final int NOT_FOUND = -1;
    private static final int INITIAL_DEPTH = 0;
    private static final int MAX_OBJECT_KEY_LENGTH_BYTES = 1024;
    private static final Pattern SAFE_CHAR_PATTERN = Pattern.compile("^[a-zA-Z0-9!\\-_.\\*'()\\/]*$");

    private final String value;

    private ObjectKey(String value) {
        this.value = validatObjectKey(value);
    }

    public static ObjectKey of(String value) {
        return new ObjectKey(value);
    }

    public String getValue() {
        return value;
    }

    private String validatObjectKey(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidObjectKeyException("Object key must not be null or empty");
        }

        if (value.startsWith("/")) {
            throw new InvalidObjectKeyException("Object key must be a relative path, not absolute");
        }

        if (value.endsWith("/")) {
            throw new InvalidObjectKeyException("Object key must not end with a slash, file name is missing");
        }

        int lengthInBytes = value.getBytes(StandardCharsets.UTF_8).length;
        if (lengthInBytes > MAX_OBJECT_KEY_LENGTH_BYTES) {
            throw new InvalidObjectKeyException("Object key is too long");
        }

        if (!isValidRelativePath(value)) {
            throw new InvalidObjectKeyException("Invalid relative path");
        }

        if (!SAFE_CHAR_PATTERN.matcher(value).matches()) {
            throw new InvalidObjectKeyException("Object key must contain valid relative path");
        }

        return value;
    }

    private boolean isValidRelativePath(String path) {
        int depth = 0;
        int startIndex = 0;

        while (startIndex < path.length()) {
            int endIndex = path.indexOf('/', startIndex);
            if (endIndex == NOT_FOUND) {
                endIndex = path.length();
            }

            int segmentLength = endIndex - startIndex;

            if (segmentLength == 0) {
                startIndex = endIndex + 1;
                continue;
            }

            if (segmentLength == 2 && path.charAt(startIndex) == '.' && path.charAt(startIndex + 1) == '.') {
                depth--;
                if (depth < INITIAL_DEPTH) {
                    return false;
                }
            } else if (!(segmentLength == 1 && path.charAt(startIndex) == '.')) {
                depth++;
            }

            startIndex = endIndex + 1;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectKey objectKey = (ObjectKey) o;
        return Objects.equals(value, objectKey.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "ObjectKey = " + value;
    }

}
