package org.websoso.s3.factory;

import org.websoso.s3.config.S3Config;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link S3Client} 인스턴스를 생성하고 캐싱하여 재사용하는 팩토리 클래스입니다.
 * <p>
 * 동일한 설정({@link S3Config})에 대해서는 하나의 {@code S3Client}만 생성되며,
 * 내부적으로 {@link ConcurrentHashMap}을 사용하여 멀티스레드 환경에서도 안전하게 동작합니다.
 * </p>
 */
public class S3ClientFactory {

    private static final Map<String, S3Client> clientCache = new ConcurrentHashMap<>();

    private S3ClientFactory() {

    }

    /**
     * 주어진 {@link S3Config}에 해당하는 {@link S3Client} 인스턴스를 반환합니다.
     * <p>
     * 캐시에 존재하지 않으면 새로 생성하여 캐시에 저장하고 반환합니다.
     * </p>
     *
     * @param s3Config S3 클라이언트 생성을 위한 구성 정보
     * @return {@link S3Client} 인스턴스
     */
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