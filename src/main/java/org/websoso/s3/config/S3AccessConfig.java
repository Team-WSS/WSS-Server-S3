package org.websoso.s3.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websoso.s3.exception.AwsCredentialsNotFoundException;
import org.websoso.s3.exception.AwsRegionNotFoundException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;

/**
 * AWS S3에 접근하기 위한 인증 정보 및 리전 설정을 담는 구성 클래스입니다.
 * <p>
 * 이 클래스는 {@link Builder}를 통해 생성하며, 다음과 같은 항목을 설정할 수 있습니다:
 * <ul>
 *   <li><b>리전(Region)</b> - 기본값은 {@code ap-northeast-2 (서울)}</li>
 *   <li><b>액세스 키 및 시크릿 키</b> - 명시하지 않으면 {@link software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider}를 사용합니다</li>
 * </ul>
 * </p>
 *
 * @see Builder
 */
public class S3AccessConfig {

    private static final Logger logger = LoggerFactory.getLogger(S3AccessConfig.class);

    private String accessKey;
    private String secretKey;
    private Region region = Region.AP_NORTHEAST_2;
    private AwsCredentialsProvider credentialsProvider;

    private S3AccessConfig() {

    }

    /**
     * {@link Builder} 인스턴스를 생성합니다.
     *
     * @return {@link Builder} 인스턴스
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@code S3Config}를 구성하기 위한 빌더 클래스입니다.
     * <p>
     * 설정 가능한 항목은 다음과 같습니다:
     *
     * <ul>
     *   <li><b>리전(Region)</b> - 명시하지 않으면 기본값으로 {@code ap-northeast-2 (서울)}을 사용합니다.</li>
     *   <li><b>액세스 키 & 시크릿 키</b> - 명시하지 않으면 환경 변수, 시스템 프로퍼티 등에서 값을 가져오는
     *       {@link software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider}를 사용합니다.</li>
     * </ul>
     * </p>
     *
     * <p>예시 사용법:</p>
     *  <pre>{@code
     *  S3AccessConfig config = S3AccessConfig.builder()
     *      .withRegion("us-west-2")
     *      .withCredentials("ACCESS_KEY", "SECRET_KEY")
     *      .build();
     *  }</pre>
     *
     */
    public static class Builder {

        private final S3AccessConfig config = new S3AccessConfig();

        /**
         * 액세스 키와 시크릿 키를 설정합니다.
         *
         * @param accessKey AWS 액세스 키
         * @param secretKey AWS 시크릿 키
         * @return Builder 인스턴스
         * @throws IllegalArgumentException 액세스 키 또는 시크릿 키가 null이거나 빈 문자열인 경우
         */
        public Builder withCredentials(String accessKey, String secretKey) {
            if (accessKey == null || accessKey.isBlank()) {
                throw new IllegalArgumentException("Access key must not be null or empty");
            }

            if (secretKey == null || secretKey.isBlank()) {
                throw new IllegalArgumentException("Secret key must not be null or empty");
            }

            config.accessKey = accessKey;
            config.secretKey = secretKey;
            return this;
        }

        /**
         * S3 리전을 설정합니다.
         * <p>
         * 기본값은 {@code ap-northeast-2 (서울)}입니다.
         * </p>
         *
         * @param region AWS 리전 문자열 (예: "us-east-1")
         * @return Builder 인스턴스
         * @throws IllegalArgumentException 리전 값이 null이거나 빈 문자열인 경우
         */
        public Builder withRegion(String region) {

            if (region == null || region.isBlank()) {
                throw new IllegalArgumentException("Region must not be null or empty");
            }

            config.region = Region.of(region);
            return this;
        }

        /**
         * 설정된 정보로 {@link S3AccessConfig} 객체를 생성합니다.
         * <p>
         * 액세스 키와 시크릿 키가 지정된 경우 {@link StaticCredentialsProvider}를 사용하며,
         * 그렇지 않은 경우 {@link DefaultCredentialsProvider}를 사용합니다.
         * 이때, 환경 변수, 시스템 속성, EC2/ECS 메타데이터 등에서 자격 증명을 불러옵니다.
         * </p>
         * <p>
         * {@link DefaultCredentialsProvider}를 사용하는 경우, 자격 증명을 미리 검증하며,
         * 유효한 자격 증명을 찾지 못하면 {@link AwsCredentialsNotFoundException}이 발생합니다.
         * </p>
         *
         * @return 구성된 {@link S3AccessConfig} 인스턴스
         * @throws AwsCredentialsNotFoundException 기본 자격 증명 체인에서 자격 증명을 찾지 못한 경우
         * @throws AwsRegionNotFoundException      리전이 유효하지 않은 경우
         */
        public S3AccessConfig build() {
            validateRegion();
            config.credentialsProvider = resolveCredentialsProvider();
            return config;
        }

        private void validateRegion() {
            if (!Region.regions().contains(config.region)) {
                throw new AwsRegionNotFoundException("Aws region could not be found");
            }
        }

        private AwsCredentialsProvider resolveCredentialsProvider() {
            // 인증 정보가 명시적으로 제공되었으면 StaticCredentialsProvider 사용
            if (config.accessKey != null && config.secretKey != null) {
                logger.debug("Using StaticCredentialsProvider with provided access key and secret key");
                return StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.accessKey, config.secretKey)
                );
            }

            // 인증 정보가 명시적으로 제공되지 않았으면,
            DefaultCredentialsProvider defaultProvider = DefaultCredentialsProvider.create();
            try {
                defaultProvider.resolveCredentials();
                logger.debug("Using DefaultCredentialsProvider");
                return defaultProvider;
            } catch (SdkClientException e) {
                throw new AwsCredentialsNotFoundException("AWS credentials could not be resolved", e);
            }
        }

    }

    public Region getRegion() {
        return region;
    }

    public AwsCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }
}