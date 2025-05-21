package org.websoso.s3.config;

/**
 * S3 이미지 처리 시 MIME 타입 감지 전략을 지정하기 위한 설정 클래스입니다.
 * <p>
 * {@link MimeDetection} 전략에 따라 빠른 감지(확장자 기반) 또는 정밀 감지(Tika 분석 기반)를 선택할 수 있습니다.
 * </p>
 */
public class S3DetectionProperties {

    /**
     * MIME 감지 전략
     */
    public enum MimeDetection {
        FAST,
        PRECISE
    }

    /**
     * 감지 전략 설정. 기본값은 {@link MimeDetection#FAST} 입니다.
     */
    private MimeDetection mimeDetection = MimeDetection.FAST;

    /**
     * MIME 감지 전략을 반환합니다.
     *
     * @return MIME 감지 전략
     */
    public MimeDetection getMimeDetection() {
        return mimeDetection;
    }

    /**
     * MIME 감지 전략을 설정합니다. null이 들어올 경우 기본값 {@link MimeDetection#FAST}로 대체됩니다.
     *
     * @param mimeDetection 감지 전략
     */
    public void setMimeDetection(MimeDetection mimeDetection) {
        this.mimeDetection = (mimeDetection != null) ? mimeDetection : MimeDetection.FAST;
    }
}