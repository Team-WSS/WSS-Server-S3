package org.websoso.s3.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
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
public class S3Config {

    private static final Logger logger = LoggerFactory.getLogger(S3Config.class);

    private String accessKey;
    private String secretKey;
    private Region region = Region.AP_NORTHEAST_2;
    private AwsCredentialsProvider credentialsProvider;

    private S3Config() {

    }

    /**
     * {@code S3Config}를 구성하기 위한 빌더 클래스입니다.
     * <p>
     * 설정 가능한 항목은 다음과 같습니다:
     *
     * <ul>
     *   <li><b>리전(Region)</b> - 기본값은 {@code ap-northeast-2 (서울)}입니다.</li>
     *   <li><b>액세스 키 & 시크릿 키</b> - 명시하지 않으면 환경 변수, 시스템 프로퍼티 등에서 값을 가져오는
     *       {@link software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider}를 사용합니다.</li>
     * </ul>
     * </p>
     */
    public static class Builder {

        private final S3Config config = new S3Config();

        public static Builder builder() {
            return new Builder();
        }

        /**
         * 액세스 키와 시크릿 키를 설정합니다.
         *
         * @param accessKey AWS 액세스 키
         * @param secretKey AWS 시크릿 키
         * @return Builder 인스턴스
         */
        public Builder withCredentials(String accessKey, String secretKey) {
            config.accessKey = accessKey;
            config.secretKey = secretKey;
            return this;
        }

        /**
         * S3 리전을 설정합니다.
         * <p>
         * 기본값은 {@code ap-northeast-2 (서울)}입니다.
         * </p>
         * @param region AWS 리전 문자열 (예: "us-east-1")
         * @return Builder 인스턴스
         */
        public Builder withRegion(String region) {
            config.region = Region.of(region);
            return this;
        }

        /**
         * 설정된 정보로 {@link S3Config} 객체를 생성합니다.
         * <p>
         * 액세스 키와 시크릿 키가 지정된 경우 {@link StaticCredentialsProvider}를,
         * 그렇지 않은 경우 {@link DefaultCredentialsProvider}를 사용합니다.
         * </p>
         * @return 구성된 {@link S3Config} 인스턴스
         */
        public S3Config build() {
            // 인증 정보가 명시적으로 제공되었으면 StaticCredentialsProvider 사용
            if (config.accessKey != null && config.secretKey != null) {
                config.credentialsProvider = StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.accessKey, config.secretKey)
                );
                logger.debug("Using StaticCredentialsProvider with provided access key");

                return config;
            }

            config.credentialsProvider = DefaultCredentialsProvider.create();
            logger.debug("Using DefaultCredentialsProvider");

            return config;
        }

    }

    public Region getRegion() {
        return region;
    }

    public AwsCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }
}