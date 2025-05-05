package org.websoso.s3.factory;

import org.websoso.s3.config.S3AccessConfig;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link S3Client} 인스턴스를 생성하고 캐싱하여 재사용하는 팩토리 클래스입니다.
 * <p>
 * 동일한 설정({@link S3AccessConfig})에 대해서는 하나의 {@code S3Client}만 생성되며,
 * 내부적으로 {@link ConcurrentHashMap}을 사용하여 멀티스레드 환경에서도 안전하게 동작합니다.
 * </p>
 */
public class S3ClientFactory {

    private static final Map<String, S3Client> clientCache = new ConcurrentHashMap<>();

    private S3ClientFactory() {

    }

    /**
     * 주어진 {@link S3AccessConfig}에 해당하는 {@link S3Client} 인스턴스를 반환합니다.
     * <p>
     * 캐시에 존재하지 않으면 새로 생성하여 캐시에 저장하고 반환합니다.
     * </p>
     *
     * @param s3AccessConfig S3 클라이언트 생성을 위한 구성 정보
     * @return {@link S3Client} 인스턴스
     */
    public static S3Client getS3Client(S3AccessConfig s3AccessConfig) {
        String cacheKey = generateCacheKey(s3AccessConfig);
        return clientCache.computeIfAbsent(cacheKey, k -> createS3Client(s3AccessConfig));
    }

    private static String generateCacheKey(S3AccessConfig s3AccessConfig) {
        return s3AccessConfig.getRegion().id() + "-" + s3AccessConfig.getCredentialsProvider().hashCode();
    }

    private static S3Client createS3Client(S3AccessConfig s3AccessConfig) {
        return S3Client.builder()
                .region(s3AccessConfig.getRegion())
                .credentialsProvider(s3AccessConfig.getCredentialsProvider())
                .build();
    }
}