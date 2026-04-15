package org.websoso.s3.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.websoso.s3.exception.validation.InvalidFileException;
import org.websoso.s3.exception.validation.InvalidObjectKeyException;
import org.websoso.s3.modle.S3UploadResult;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

class S3FileServiceTest {

    private S3Client s3Client;
    private S3FileService fileService;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        fileService = new S3FileService(s3Client, "test-bucket");
    }

    @DisplayName("[BUG-1] upload(File) - S3 실패 응답 시 isSuccess=false를 반환한다")
    @Test
    void uploadFile_whenS3ReturnsFailure_returnsFailResult() {
        // given
        File file = new File("src/test/resources/test.png");
        stubFailingPutObject();

        // when
        S3UploadResult result = fileService.upload("test/test.png", file);

        // then
        assertThat(result.isSuccess()).isFalse();
        verify(s3Client, never()).utilities(); // URL 조회가 호출되어선 안 된다
    }

    @DisplayName("[BUG-1] upload(File, contentType) - S3 실패 응답 시 isSuccess=false를 반환한다")
    @Test
    void uploadFileWithContentType_whenS3ReturnsFailure_returnsFailResult() {
        // given
        File file = new File("src/test/resources/test.png");
        stubFailingPutObject();

        // when
        S3UploadResult result = fileService.upload("test/test.png", file, "image/png");

        // then
        assertThat(result.isSuccess()).isFalse();
        verify(s3Client, never()).utilities();
    }

    @DisplayName("[BUG-1] upload(InputStream) - S3 실패 응답 시 isSuccess=false를 반환한다")
    @Test
    void uploadInputStream_whenS3ReturnsFailure_returnsFailResult() {
        // given
        ByteArrayInputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        stubFailingPutObject();

        // when
        S3UploadResult result = fileService.upload("test/file.bin", stream, "application/octet-stream", 3);

        // then
        assertThat(result.isSuccess()).isFalse();
        verify(s3Client, never()).utilities();
    }

    @DisplayName("upload(File) - S3 성공 시 isSuccess=true이고 URL을 반환한다")
    @Test
    void uploadFile_whenS3ReturnsSuccess_returnsSuccessResult() throws MalformedURLException {
        // given
        File file = new File("src/test/resources/test.png");
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/test/test.png";
        stubSuccessfulPutObject("\"etag-abc\"");
        stubGetUrl(expectedUrl);

        // when
        S3UploadResult result = fileService.upload("test/test.png", file);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.url()).isEqualTo(expectedUrl);
        assertThat(result.eTag()).isEqualTo("\"etag-abc\"");
    }

    @DisplayName("key가 null이면 InvalidObjectKeyException을 던진다")
    @Test
    void upload_nullKey_throwsIllegalArgumentException() {
        File file = new File("src/test/resources/test.png");

        assertThatThrownBy(() -> fileService.upload(null, file))
                .isInstanceOf(InvalidObjectKeyException.class);
    }

    @DisplayName("존재하지 않는 파일이면 InvalidFileException을 던진다")
    @Test
    void upload_nonExistentFile_throwsInvalidFileException() {
        File file = new File("does-not-exist.png");

        assertThatThrownBy(() -> fileService.upload("key", file))
                .isInstanceOf(InvalidFileException.class);
    }

    private void stubFailingPutObject() {
        SdkHttpResponse failHttp = mock(SdkHttpResponse.class);
        when(failHttp.isSuccessful()).thenReturn(false);
        when(failHttp.statusCode()).thenReturn(403);
        when(failHttp.statusText()).thenReturn(Optional.of("Forbidden"));

        PutObjectResponse failResponse = mock(PutObjectResponse.class);
        when(failResponse.sdkHttpResponse()).thenReturn(failHttp);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(failResponse);
    }

    private void stubSuccessfulPutObject(String eTag) {
        SdkHttpResponse okHttp = mock(SdkHttpResponse.class);
        when(okHttp.isSuccessful()).thenReturn(true);
        when(okHttp.statusCode()).thenReturn(200);
        when(okHttp.statusText()).thenReturn(Optional.of("OK"));

        PutObjectResponse okResponse = mock(PutObjectResponse.class);
        when(okResponse.sdkHttpResponse()).thenReturn(okHttp);
        when(okResponse.eTag()).thenReturn(eTag);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(okResponse);
    }

    private void stubGetUrl(String url) throws MalformedURLException {
        S3Utilities utilities = mock(S3Utilities.class);
        when(s3Client.utilities()).thenReturn(utilities);
        when(utilities.getUrl(any(GetUrlRequest.class))).thenReturn(new URL(url));
    }
}
