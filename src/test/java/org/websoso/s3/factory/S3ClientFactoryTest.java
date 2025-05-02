package org.websoso.s3.factory;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.websoso.s3.config.S3Config;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class S3ClientFactoryTest {

    private S3Config s3Config;
    private AwsCredentialsProvider mockCredentials;

    @BeforeEach
    void setUp() {
        mockCredentials = mock(AwsCredentialsProvider.class);
        s3Config = mock(S3Config.class);

        when(s3Config.getRegion()).thenReturn(Region.AP_NORTHEAST_2);
        when(s3Config.getCredentialsProvider()).thenReturn(mockCredentials);
    }

    @DisplayName("같은 S3Config로 여러 번 getS3Client() 호출 시 동일한 S3Client를 반환한다.")
    @Test
    void shouldReturnSameS3ClientInstanceForSameConfig() {
        // when
        S3Client client1 = S3ClientFactory.getS3Client(s3Config);
        S3Client client2 = S3ClientFactory.getS3Client(s3Config);

        // then
        assertThat(client1).isNotNull();
        assertThat(client2).isNotNull();
        assertThat(client1).isSameAs(client2);
    }

    @DisplayName("동일한 리전과 동일한 인증 정보를 가진 S3Config는 동일한 S3Client를 반환한다.")
    @Test
    void shouldReturnSameS3ClientForConfigsWithSameRegionAndCredentials() {
        // given
        Region region = Region.AP_NORTHEAST_2;

        S3Config config1 = mock(S3Config.class);
        when(config1.getRegion()).thenReturn(region);
        when(config1.getCredentialsProvider()).thenReturn(mockCredentials);

        S3Config config2 = mock(S3Config.class);
        when(config2.getRegion()).thenReturn(region);
        when(config2.getCredentialsProvider()).thenReturn(mockCredentials);

        // when
        S3Client client1 = S3ClientFactory.getS3Client(config1);
        S3Client client2 = S3ClientFactory.getS3Client(config2);

        // then
        assertThat(client1).isSameAs(client2);
    }

    @DisplayName("멀티스레드 환경에서도 동일한 S3Config에 대해 동일한 S3Client를 반환한다.")
    @Test
    void shouldReturnSameS3ClientInstanceInMultithreadedEnvironment() throws Exception {
        // given
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<S3Client>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> S3ClientFactory.getS3Client(s3Config)));
        }

        Set<S3Client> instanceSet = new HashSet<>();
        for (Future<S3Client> future : futures) {
            instanceSet.add(future.get());
        }

        executor.shutdown();
        assertThat((executor.awaitTermination(10, TimeUnit.SECONDS))).isTrue();

        // then
        assertThat(instanceSet).hasSize(1);
    }

    @DisplayName("서로 다른 S3Config를 사용하면 서로 다른 S3Client를 생성한다.")
    @Test
    void shouldCreateDifferentS3ClientInstancesForDifferentConfigs() {
        // given
        AwsCredentialsProvider credentials1 = mock(AwsCredentialsProvider.class);
        S3Config config1 = mock(S3Config.class);
        when(config1.getRegion()).thenReturn(Region.AP_NORTHEAST_2);
        when(config1.getCredentialsProvider()).thenReturn(credentials1);

        AwsCredentialsProvider credentials2 = mock(AwsCredentialsProvider.class);
        S3Config config2 = mock(S3Config.class);
        when(config2.getRegion()).thenReturn(Region.US_WEST_1);
        when(config2.getCredentialsProvider()).thenReturn(credentials2);

        // when
        S3Client client1 = S3ClientFactory.getS3Client(config1);
        S3Client client2 = S3ClientFactory.getS3Client(config2);

        // then
        assertThat(client1).isNotNull();
        assertThat(client2).isNotNull();
        assertThat(client1).isNotSameAs(client2);
    }

}
