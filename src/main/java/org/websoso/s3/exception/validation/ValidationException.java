package org.websoso.s3.exception.validation;

/**
 * 라이브러리 또는 애플리케이션의 내부 로직을 실행하기 전, 입력 값의 유효성 검사에 실패했을 때 발생하는 예외의 최상위 기본 클래스입니다.
 * <p>
 * 이 예외는 잘못된 데이터가 핵심 비즈니스 로직으로 전달되는 것을 막는 '가드(Guard)' 역할을 합니다.
 * 주로 외부로부터 받은 데이터가 사전에 정의된 규칙이나 제약 조건을 위반했을 경우 사용됩니다.
 *
 * 이 클래스를 상속하는 구체적인 예외들은 어떤 유효성 검사 규칙이 위반되었는지 명확하게 나타냅니다.
 * (예: {@code InvalidBucketNameException}, {@code InvalidFileException})
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}