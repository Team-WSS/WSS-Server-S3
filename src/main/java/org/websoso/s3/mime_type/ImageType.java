package org.websoso.s3.mime_type;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ImageType {
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png");

    private final String extension;
    private final String mimeType;

    ImageType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static Set<String> getAllowedMimeTypes() {
        return Arrays.stream(values())
                .map(ImageType::getMimeType)
                .collect(Collectors.toSet());
    }

    public static Set<String> getAllowedExtensions() {
        return Arrays.stream(values())
                .map(imageType -> "." + imageType.getExtension().toLowerCase())
                .collect(Collectors.toSet());
    }

}
