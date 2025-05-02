package org.websoso.s3.factory;

import org.websoso.s3.config.S3Config;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class S3ClientFactory {
    private static final Map<String, S3Client> clientCache = new ConcurrentHashMap<>();

    private S3ClientFactory() { } // 인스턴스화 방지

    public static S3Client getS3Client(S3Config s3Config) {
        String cacheKey = generateCacheKey(s3Config);
        return clientCache.computeIfAbsent(cacheKey, k -> createS3Client(s3Config));
    }

    private static String generateCacheKey(S3Config s3Config) {
        return s3Config.getRegion().id() + "-" + s3Config.getCredentialsProvider().hashCode();
    }

    private static S3Client createS3Client(S3Config s3Config) {
        return S3Client.builder()
                .region(s3Config.getRegion())
                .credentialsProvider(s3Config.getCredentialsProvider())
                .build();
    }
}