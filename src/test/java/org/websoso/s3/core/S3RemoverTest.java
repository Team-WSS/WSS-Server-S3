package org.websoso.s3.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.websoso.s3.exception.aws.s3.S3AccessDeniedException;
import org.websoso.s3.exception.aws.s3.S3BucketNotFoundException;
import org.websoso.s3.exception.aws.s3.S3OperationException;
import org.websoso.s3.modle.Bucket;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3RemoverTest {

    private S3Remover s3Remover;

    @Mock
    private S3Client s3Client;

    private final Bucket testBucket = Bucket.of("test-bucket");
    private final ObjectKey testObjectKey = ObjectKey.of("test-folder/test-file.jpg");

    @BeforeEach
    void setUp() {
        s3Remover = new S3Remover(s3Client, testBucket);
    }

    @Test
    @DisplayName("성공: S3 객체를 정상적으로 삭제하고 true를 반환한다.")
    void delete_Success() {
        // Given
        DeleteObjectResponse mockResponse = DeleteObjectResponse.builder().build();
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(mockResponse);

        // When
        boolean result = s3Remover.delete(testObjectKey);

        // Then
        assertThat(result).isTrue();
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("실패(서비스 오류): 버킷이 없으면, AWS 상세 정보를 포함한 S3BucketNotFoundException을 던진다.")
    void delete_ThrowsS3BucketNotFoundException_WhenNoSuchBucket() {
        // Given
        S3Exception s3Exception = createMockS3Exception("NoSuchBucket", "The specified bucket does not exist.", 404);
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(s3Exception);

        // When & Then
        assertThatThrownBy(() -> s3Remover.delete(testObjectKey))
                .isInstanceOf(S3BucketNotFoundException.class)
                .hasMessageContaining("S3 Bucket not found: " + testBucket.getValue())
                .satisfies(ex -> {
                    S3BucketNotFoundException thrown = (S3BucketNotFoundException) ex;
                    assertThat(thrown.getAwsErrorCode()).isEqualTo("NoSuchBucket");
                    assertThat(thrown.getHttpStatusCode()).isEqualTo(404);
                    assertThat(thrown.getAwsRequestId()).isEqualTo("TEST_REQUEST_ID_123");
                });
    }

    @Test
    @DisplayName("실패(서비스 오류): 접근 권한이 없으면, AWS 상세 정보를 포함한 S3AccessDeniedException을 던진다.")
    void delete_ThrowsS3AccessDeniedException_WhenAccessDenied() {
        // Given
        S3Exception s3Exception = createMockS3Exception("AccessDenied", "Access Denied", 403);
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(s3Exception);

        // When & Then
        assertThatThrownBy(() -> s3Remover.delete(testObjectKey))
                .isInstanceOf(S3AccessDeniedException.class)
                .hasMessageContaining("S3 Access denied for key")
                .satisfies(ex -> {
                    S3AccessDeniedException thrown = (S3AccessDeniedException) ex;
                    assertThat(thrown.getAwsErrorCode()).isEqualTo("AccessDenied");
                    assertThat(thrown.getHttpStatusCode()).isEqualTo(403);
                    assertThat(thrown.getAwsRequestId()).isEqualTo("TEST_REQUEST_ID_123");
                });
    }

    @Test
    @DisplayName("실패(서비스 오류): 처리되지 않은 S3 오류 시, AWS 상세 정보를 포함한 S3OperationException을 던진다.")
    void delete_ThrowsS3OperationException_ForUnhandledS3Error() {
        // Given
        S3Exception s3Exception = createMockS3Exception("InvalidRequest", "Invalid Request", 400);
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(s3Exception);

        // When & Then
        assertThatThrownBy(() -> s3Remover.delete(testObjectKey))
                .isInstanceOf(S3OperationException.class)
                .isNotInstanceOfAny(S3BucketNotFoundException.class, S3AccessDeniedException.class)
                .hasMessageContaining("The AWS Error Code is INVALID_REQUEST")
                .satisfies(ex -> {
                    S3OperationException thrown = (S3OperationException) ex;
                    assertThat(thrown.getAwsErrorCode()).isEqualTo("InvalidRequest");
                    assertThat(thrown.getHttpStatusCode()).isEqualTo(400);
                    assertThat(thrown.getAwsRequestId()).isEqualTo("TEST_REQUEST_ID_123");
                });
    }

    @Test
    @DisplayName("실패(서비스 오류): 일반 SdkServiceException 발생 시, RequestId 등은 포함하지만 AwsErrorCode는 없는 S3OperationException을 던진다.")
    void delete_ThrowsS3OperationException_ForGenericSdkServiceException() {
        // Given
        SdkServiceException serviceException = SdkServiceException.builder()
                .message("Internal server error")
                .statusCode(500)
                .requestId("GENERIC_REQUEST_ID_789")
                .build();
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(serviceException);

        // When & Then
        assertThatThrownBy(() -> s3Remover.delete(testObjectKey))
                .isInstanceOf(S3OperationException.class)
                .hasMessageContaining("due to a generic S3 service error")
                .satisfies(ex -> {
                    S3OperationException thrown = (S3OperationException) ex;
                    assertThat(thrown.getAwsErrorCode()).isNull();
                    assertThat(thrown.getHttpStatusCode()).isEqualTo(500);
                    assertThat(thrown.getAwsRequestId()).isEqualTo("GENERIC_REQUEST_ID_789");
                });
    }

    @Test
    @DisplayName("실패(클라이언트 오류): SdkClientException 발생 시, AWS 상세 정보가 없는 S3OperationException을 던진다.")
    void delete_ThrowsS3OperationException_ForSdkClientException() {
        // Given
        SdkClientException clientException = SdkClientException.builder().message("Unable to connect").build();
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(clientException);

        // When & Then
        assertThatThrownBy(() -> s3Remover.delete(testObjectKey))
                .isInstanceOf(S3OperationException.class)
                .hasMessageContaining("Failed to communicate with S3 to delete")
                .satisfies(ex -> {
                    S3OperationException thrown = (S3OperationException) ex;
                    assertThat(thrown.getAwsErrorCode()).isNull();
                    assertThat(thrown.getHttpStatusCode()).isEqualTo(-1);
                    assertThat(thrown.getAwsRequestId()).isNull();
                });
    }

    private S3Exception createMockS3Exception(String errorCode, String errorMessage, int statusCode) {
        AwsErrorDetails errorDetails = AwsErrorDetails.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();

        return (S3Exception) S3Exception.builder()
                .awsErrorDetails(errorDetails)
                .statusCode(statusCode)
                .requestId("TEST_REQUEST_ID_123")
                .extendedRequestId("TEST_EXTENDED_REQUEST_ID_456")
                .build();
    }
}