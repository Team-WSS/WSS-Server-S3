package org.websoso.s3.factory;

import org.websoso.s3.config.S3DetectionProperties;
import org.websoso.s3.core.strategy.FastMimeTypeDetectionStrategy;
import org.websoso.s3.core.strategy.MimeTypeDetectionStrategy;
import org.websoso.s3.core.strategy.PreciseMimeTypeDetectionStrategy;

/**
 * MIME 타입 감지 전략을 설정에 따라 생성하는 팩토리 클래스입니다.
 */
public final class MimeTypeDetectionStrategyFactory {

    private MimeTypeDetectionStrategyFactory() {
        // prevent instantiation
    }

    /**
     * 설정에 기반한 {@link MimeTypeDetectionStrategy} 구현체를 반환합니다.
     *
     * @param properties S3 감지 설정
     * @return 전략 구현체 인스턴스
     */
    public static MimeTypeDetectionStrategy from(S3DetectionProperties properties) {
        return switch (properties.getMimeDetection()) {
            case PRECISE -> new PreciseMimeTypeDetectionStrategy();
            case FAST -> new FastMimeTypeDetectionStrategy();
            default -> throw new IllegalArgumentException("Unsupported mime detection mode");
        };
    }
}
