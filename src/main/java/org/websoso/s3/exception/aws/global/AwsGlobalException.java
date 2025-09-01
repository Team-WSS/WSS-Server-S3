package org.websoso.s3.exception.aws.global;

/**
 * AWS 서비스와 상호작용하기 위한 전제 조건이 충족되지 않았을 때 발생하는 예외의 최상위 기본 클래스입니다.
 * <p>
 * 이 예외는 특정 AWS 서비스(예: S3)의 API 호출 단계 이전에 발생하는 설정 및 환경 관련 문제를 다룹니다.
 * 주로 AWS 클라이언트 초기화나 설정 과정에서 필요한 정보가 누락되었거나 유효하지 않을 때 사용됩니다. (예: 리전 설정 문제, 자격 증명 문제)
 *
 * 이 클래스를 상속하는 구체적인 예외들은 어떤 전역 설정에 문제가 있는지 명확히 알려주는 역할을 합니다.
 * (예: {@code AwsCredentialsNotFoundException}, {@code AwsRegionNotfoundException})
 */

public class AwsGlobalException extends RuntimeException {

    public AwsGlobalException(String message) {
        super(message);
    }

    public AwsGlobalException(String message, Throwable cause) {
        super(message, cause);
    }

}
