# WSS-Server-S3

AWS SDK for Java V2를 랩핑한 S3 라이브러리입니다. 간편하게 S3를 사용할 수 있는 인터페이스를 제공합니다.


## Features

- S3 파일 업로드 / 삭제를 지원합니다.
- 다양한 입력 타입(File, InputStream) 지원을 지원합니다. 

## Installation

### Gradle (JitPack)

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Team-WSS:WSS-Server-S3:{version}'
}
```

### Maven (JitPack)

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>com.github.Team-WSS</groupId>
    <artifactId>WSS-Server-S3</artifactId>
    <version>{version}</version>
  </dependency>
</dependencies>
```

## Getting Started

간단한 사용 방법을 단계별로 소개합니다.

1. **S3 클라이언트 설정**

```java
S3AccessConfig s3Config = S3AccessConfig.builder()
    // .withRegion("REGION")
    .withCredentials("YOUR_ACCESS_KEY", "YOUR_SECRET_KEY")
    .build();

S3Client s3Client = S3ClientFactory.getS3Client(s3Config);
```
| 설정 키       | 설명               | 필수 여부 | 기본값             |
| ---------- | ---------------- |-------|-----------------|
| accessKey  | AWS Access Key   | 선택    | `환경 변수`, `시스템 프로퍼티` |
| secretKey  | AWS Secret Key   | 선택    | `환경 변수`, `시스템 프로퍼티` |
| region     | AWS Region       | 선택    | `ap-northeast-2` |



2. **파일 서비스 인스턴스 생성**

```java
S3FileService s3Service = new S3FileService(s3Client, "your-bucket-name");
```

3. **파일 업로드**

```java
File file = new File("path/to/your/file.jpg");
S3UploadResult result = s3Service.upload("folder/filename.jpg", file);

if (result.isSuccess()) {
    System.out.println("업로드 성공: " + result.getUrl());
} else {
    throw new RuntimeException("파일 업로드 실패");
}
```

### Spring 환경 통합 예시

```java
@Configuration
public class S3Config {
    @Bean
    public S3AccessConfig s3AccessConfig() {
        return S3AccessConfig.builder()
                .withCredentials("ACCESS_KEY", "SECRET_KEY")
                .build();
    }

    @Bean
    public S3Client s3Client(S3AccessConfig s3AccessConfig) {
        return S3ClientFactory.getS3Client(s3AccessConfig);
    }

    @Bean
    public S3FileService s3FileService(S3Client s3Client) {
        return new S3FileService(s3Client, "BUCKET_NAME");
    }
}

@Service
public class FileService {
    
    private final S3FileService s3Service;

    public FileService(S3FileService s3FileService) {
        this.s3Service = s3FileService;
    }

    public String upload(String key, File file) {
        
        S3UploadResult result = s3Service.upload(key, file);
        
        if (result.isSuccess()) {
            return result.getUrl();
        }
        
        throw new RuntimeException("파일 업로드 실패");
    }
    
    ...
    
}
```

## 테스트

```bash
./gradlew test
```