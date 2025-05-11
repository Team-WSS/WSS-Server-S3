package org.websoso.s3.core;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

enum ImageType {
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png");

    private final String extension;
    private final String mimeType;

    ImageType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    String getExtension() {
        return extension;
    }

    String getMimeType() {
        return mimeType;
    }

    static Set<String> getAllowedMimeTypes() {
        return Arrays.stream(values())
                .map(ImageType::getMimeType)
                .collect(Collectors.toSet());
    }

    static Set<String> getAllowedExtensions() {
        return Arrays.stream(values())
                .map(imageType -> "." + imageType.getExtension().toLowerCase())
                .collect(Collectors.toSet());
    }
}
