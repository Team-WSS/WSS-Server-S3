package org.websoso.s3.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

/**
 * AWS S3에 접근하기 위해 필요한 인증 정보와 리전 설정을 구성하는 클래스 <br>
 * - 리전의 기본 값은 "ap-northeast-2 (서울)"이다. <br>
 * - 액세스 키와 시크릿 키를 구성하지 않으면, 환경 정보 등에서 가져오는 기본 공급자를 사용한다.
 */
public class S3Config {

    private static final Logger logger = LoggerFactory.getLogger(S3Config.class);

    private String accessKey;
    private String secretKey;
    private Region region = Region.AP_NORTHEAST_2;
    private AwsCredentialsProvider credentialsProvider;

    private S3Config() {

    }

    public static class Builder {

        private final S3Config config = new S3Config();

        public static Builder builder() {
            return new Builder();
        }

        public Builder withCredentials(String accessKey, String secretKey) {
            config.accessKey = accessKey;
            config.secretKey = secretKey;
            return this;
        }

        /**
         * 리전 설정 (기본값: ap-northeast-2)
         */
        public Builder withRegion(String region) {
            config.region = Region.of(region);
            return this;
        }

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