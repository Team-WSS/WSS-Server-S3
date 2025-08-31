package org.websoso.s3.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.websoso.s3.exception.aws.s3.S3AccessDeniedException;
import org.websoso.s3.exception.aws.s3.S3BucketNotFoundException;
import org.websoso.s3.exception.aws.s3.S3EntityTooLargeException;
import org.websoso.s3.exception.aws.s3.S3OperationException;
import org.websoso.s3.modle.Bucket;
import org.websoso.s3.modle.S3UploadResponse;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3UploaderTest {

    private S3Uploader s3Uploader;

    @Mock
    private S3Client s3Client;

    private File tempFile;

    @TempDir
    Path tempDir;

    private InputStream mockInputStream;

    private final Bucket testBucket = Bucket.of("test-bucket");
    private final ObjectKey testObjectKey = ObjectKey.of("test-folder/test-file.txt");
    private final ContentType testContentType = ContentType.of("text/plain");
    private final ContentLength testContentLength = ContentLength.of(128L);

    @BeforeEach
    void setUp() throws IOException {
        s3Uploader = new S3Uploader(s3Client, testBucket);
        mockInputStream = new ByteArrayInputStream("test data".getBytes());
        tempFile = Files.createFile(tempDir.resolve("test-file.txt")).toFile();
    }

    @Test
    @DisplayName("성공: File을 정상적으로 업로드하고 S3UploadResponse를 반환한다.")
    void uploadFile_Success() {
        // Given
        SdkHttpResponse mockSdkHttpResponse = SdkHttpResponse.builder().statusCode(200).statusText("OK").build();
        PutObjectResponse mockResponse = mock(PutObjectResponse.class);
        when(mockResponse.eTag()).thenReturn("\"mock-etag-123\"");
        when(mockResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(mockResponse);

        // When
        S3UploadResponse result = s3Uploader.upload(testObjectKey, tempFile);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.eTag()).isEqualTo("\"mock-etag-123\"");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("성공: InputStream을 정상적으로 업로드하고 S3UploadResponse를 반환한다.")
    void uploadInputStream_Success() {
        // Given
        SdkHttpResponse mockSdkHttpResponse = SdkHttpResponse.builder().statusCode(200).statusText("OK").build();
        PutObjectResponse mockResponse = mock(PutObjectResponse.class);
        when(mockResponse.eTag()).thenReturn("\"mock-etag-456\"");
        when(mockResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(mockResponse);

        // When
        S3UploadResponse result = s3Uploader.upload(testObjectKey, mockInputStream, testContentType, testContentLength);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.eTag()).isEqualTo("\"mock-etag-456\"");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("성공: File과 ContentType을 함께 사용해 정상적으로 업로드하고 S3UploadResponse를 반환한다.")
    void uploadFileWithContentType_Success() {
        // Given
        SdkHttpResponse mockSdkHttpResponse = SdkHttpResponse.builder().statusCode(200).statusText("OK").build();
        PutObjectResponse mockResponse = mock(PutObjectResponse.class);
        when(mockResponse.eTag()).thenReturn("\"mock-etag-789\"");
        when(mockResponse.sdkHttpResponse()).thenReturn(mockSdkHttpResponse);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(mockResponse);

        // When
        S3UploadResponse result = s3Uploader.upload(testObjectKey, tempFile, testContentType);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.eTag()).isEqualTo("\"mock-etag-789\"");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("실패(서비스 오류): 버킷이 없으면, AWS 상세 정보를 포함한 S3BucketNotFoundException을 던진다.")
    void upload_ThrowsS3BucketNotFoundException_WhenNoSuchBucket() {
        // Given
        S3Exception s3Exception = createMockS3Exception("NoSuchBucket", "The specified bucket does not exist.", 404);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(s3Exception);

        // When & Then
        assertThatThrownBy(() -> s3Uploader.upload(testObjectKey, tempFile))
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
    void upload_ThrowsS3AccessDeniedException_WhenAccessDenied() {
        // Given
        S3Exception s3Exception = createMockS3Exception("AccessDenied", "Access Denied", 403);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(s3Exception);

        // When & Then
        assertThatThrownBy(() -> s3Uploader.upload(testObjectKey, tempFile))
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
    @DisplayName("실패(서비스 오류): 파일 크기가 너무 크면, AWS 상세 정보를 포함한 S3EntityTooLargeException을 던진다.")
    void upload_ThrowsS3EntityTooLargeException_WhenEntityTooLarge() {
        // Given
        S3Exception s3Exception = createMockS3Exception("EntityTooLarge", "Your proposed upload exceeds the maximum allowed size", 400);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(s3Exception);

        // When & Then
        assertThatThrownBy(() -> s3Uploader.upload(testObjectKey, tempFile))
                .isInstanceOf(S3EntityTooLargeException.class)
                .hasMessageContaining("exceeds the limit")
                .satisfies(ex -> {
                    S3EntityTooLargeException thrown = (S3EntityTooLargeException) ex;
                    assertThat(thrown.getAwsErrorCode()).isEqualTo("EntityTooLarge");
                    assertThat(thrown.getHttpStatusCode()).isEqualTo(400);
                    assertThat(thrown.getAwsRequestId()).isEqualTo("TEST_REQUEST_ID_123");
                });
    }

    @Test
    @DisplayName("실패(서비스 오류): 처리되지 않은 S3 오류 시, AWS 상세 정보를 포함한 S3OperationException을 던진다.")
    void upload_ThrowsS3OperationException_ForUnhandledS3Error() {
        // Given
        S3Exception s3Exception = createMockS3Exception("InvalidRequest", "Invalid Request", 400);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(s3Exception);

        // When & Then
        assertThatThrownBy(() -> s3Uploader.upload(testObjectKey, tempFile))
                .isInstanceOf(S3OperationException.class)
                .isNotInstanceOfAny(S3BucketNotFoundException.class, S3AccessDeniedException.class, S3EntityTooLargeException.class)
                .hasMessageContaining("The AWS Error Code is INVALID_REQUEST")
                .satisfies(ex -> {
                    S3OperationException thrown = (S3OperationException) ex;
                    assertThat(thrown.getAwsErrorCode()).isEqualTo("InvalidRequest");
                    assertThat(thrown.getHttpStatusCode()).isEqualTo(400);
                    assertThat(thrown.getAwsRequestId()).isEqualTo("TEST_REQUEST_ID_123");
                });
    }

    @Test
    @DisplayName("실패(서비스 오류): 일반 SdkServiceException 발생 시, S3OperationException을 던진다.")
    void upload_ThrowsS3OperationException_ForGenericSdkServiceException() {
        // Given
        SdkServiceException serviceException = SdkServiceException.builder()
                .message("Internal server error")
                .statusCode(500)
                .requestId("GENERIC_REQUEST_ID_789")
                .build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(serviceException);

        // When & Then
        assertThatThrownBy(() -> s3Uploader.upload(testObjectKey, tempFile))
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
    @DisplayName("실패(클라이언트 오류): SdkClientException 발생 시, S3OperationException을 던진다.")
    void upload_ThrowsS3OperationException_ForSdkClientException() {
        // Given
        SdkClientException clientException = SdkClientException.builder().message("Unable to connect").build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(clientException);

        // When & Then
        assertThatThrownBy(() -> s3Uploader.upload(testObjectKey, tempFile))
                .isInstanceOf(S3OperationException.class)
                .hasMessageContaining("Failed to communicate with S3 to upload")
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

