package org.websoso.s3.exception.aws.s3;

/**
 * Amazon S3와 관련된 작업 중에 발생하는 모든 오류에 대한 기본 예외 클래스입니다.
 * <p>
 * 이 클래스는 AWS SDK에서 발생하는 클라이언트 측 오류와 서비스 측 오류를 라이브러리 사용자를 위해
 * 일관된 단일 예외 계층으로 통합하는 역할을 합니다.
 *
 * 이 예외는 크게 두 가지 주요 실패 시나리오를 구분합니다:
 * <ul>
 * <li><b>서비스 오류 (Service Errors):</b> 요청이 S3 서비스에 성공적으로 도달했지만, 서비스 측에서
 * 요청을 처리할 수 없어 거부된 경우 (예: '버킷을 찾을 수 없음') 발생합니다. 이 경우, 예외 객체는
 * {@code awsErrorCode}, {@code awsRequestId}, {@code httpStatusCode}와 같이
 * AWS로부터 받은 상세한 컨텍스트 정보를 포함합니다.</li>
 *
 * <li><b>클라이언트 오류 (Client Errors):</b> 요청이 S3 서비스에 도달하기 전에 실패한 경우
 * (예: 네트워크 연결 실패) 발생합니다. 이러한 경우, AWS 관련 상세 오류 정보는 존재하지 않으므로
 * 이 예외의 해당 필드들은 {@code null} 또는 기본값(-1)을 갖게 됩니다.</li>
 * </ul>
 * 이 통합된 접근 방식을 통해 라이브러리 사용자는 모든 S3 관련 문제를 일관된 `catch` 구문으로 처리하면서도,
 * 상세 정보가 필요할 때는 해당 정보에 접근할 수 있습니다.
 */
public class S3OperationException extends RuntimeException {

    private final String awsErrorCode;
    private final String awsRequestId;
    private final String awsExtendedRequestId;
    private final int httpStatusCode;

    public S3OperationException(String message, Throwable cause, String awsErrorCode, String awsRequestId, String awsExtendedRequestId, int httpStatusCode) {
        super(message, cause);
        this.awsErrorCode = awsErrorCode;
        this.awsRequestId = awsRequestId;
        this.awsExtendedRequestId = awsExtendedRequestId;
        this.httpStatusCode = httpStatusCode;
    }

    public S3OperationException(String message, Throwable cause) {
        this(message, cause, null, null, null, -1);
    }

    public String getAwsErrorCode() {
        return awsErrorCode;
    }

    public String getAwsRequestId() {
        return awsRequestId;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public String getMessage() {
        return super.getMessage() +
                " (HTTP Status Code: " + httpStatusCode +
                ", AWS Error Code: " + awsErrorCode +
                ", AWS Extended Request ID: " + awsExtendedRequestId +
                ", AWS Request ID: " + awsRequestId + ")";
    }
}

