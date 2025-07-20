# WSS S3 Service

## 1. 개요

AWS SDK for Java V2를 기반으로, AWS S3(Simple Storage Service)와의 상호작용을 간소화하고 파일 및 이미지 업로드/조회/삭제 기능을 안정적이고 편리하게 사용할 수 있도록 설계된 Java 라이브러리입니다.

본 라이브러리는 S3의 핵심 기능들을 추상화하여 제공하며, 특히 이미지 파일의 MIME 타입 감지, 콘텐츠 크기 검증 등과 같은 부가적인 기능들을 포함하여 개발자가 비즈니스 로직에 더 집중할 수 있도록 돕습니다.

## 2. 주요 기능

- **간편한 파일 및 이미지 관리**: `S3FileService`와 `S3ImageService`를 통해 인터페이스`S3DefaultService`로 파일을 업로드, 조회, 삭제할 수 있습니다.
- **유연한 업로드 방식**: `File` 객체와 `InputStream`을 모두 지원하여 다양한 환경에 적용할 수 있습니다.
- **안전한 이미지 업로드 (MIME 타입 검증)**: `S3ImageService`를 통해 이미지 업로드 시, 파일의 유효성을 검증하기 위해 MIME 타입을 감지합니다.
    - **고속(Fast) 전략**: 파일 확장자를 기반으로 빠르게 타입을 감지합니다.
    - **정밀(Precise) 전략**: 파일의 매직 넘버(Magic Number)를 분석하여 정확하게 타입을 감지합니다. (Apache Tika 사용)
- **강타입(Strongly-typed) 모델**: `Key`, `Bucket`, `AccessKey` 등 S3의 주요 개념들을 클래스로 래핑하여 컴파일 타임에 실수를 방지하고 코드의 안정성을 높입니다.
- **세분화된 커스텀 예외**: `AWSBucketNotFoundException`, `InvalidContentTypeException` 등 발생할 수 있는 다양한 예외 상황에 대한 명확한 예외 클래스를 제공하여 오류 처리를 용이하게 합니다.
- **유연한 설정**: `S3AccessConfig`를 통해 AWS 자격 증명(Credentials) 및 리전(Region)을 쉽게 설정할 수 있습니다. 환경 변수, 시스템 프로퍼티를 통한 자동 설정도 지원합니다.
- **팩토리 및 캐싱**: `S3ClientFactory`와 `MimeTypeDetectionStrategyFactory`를 통해 S3 클라이언트와 MIME 타입 감지 전략을 유연하게 생성하고, 생성된 S3 클라이언트를 캐싱하여 성능을 최적화합니다.

## 3. 설치 방법

### Gradle (JitPack)

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Team-WSS:WSS-Server-S3:{latest-version}'
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
    <version>{latest-version}</version>
  </dependency>
</dependencies>
```
> `{latest-version}` 부분에는 [Releases](https://github.com/Team-WSS/WSS-Server-S3/releases) 페이지에서 최신 버전을 확인하여 입력해주세요.

## 4. 사용 방법

### 4.1. 기본 사용법

1. **S3 클라이언트 설정**

   `S3AccessConfig` 빌더를 사용하여 AWS 자격 증명과 리전을 설정합니다.
    - 자격 증명을 명시하지 않으면, 라이브러리는 환경 변수(`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`), 시스템 프로퍼티 등에서 자동으로 값을 탐색합니다.
    - 리전을 명시하지 않으면 기본값으로 `ap-northeast-2` (서울)가 사용됩니다.

   ```java
   S3AccessConfig s3Config = S3AccessConfig.builder()
       .withCredentials("YOUR_ACCESS_KEY", "YOUR_SECRET_KEY")
       .withRegion("ap-northeast-2")
       .build();

   S3Client s3Client = S3ClientFactory.getS3Client(s3Config);
   ```

2. **서비스 인스턴스 생성**

    - **일반 파일용**: `S3FileService`
    - **이미지 파일용**: `S3ImageService` (MIME 타입 감지 전략 필요)

   ```java
   // 일반 파일 서비스
   S3FileService fileService = new S3FileService(s3Client, "your-bucket-name");

   // 이미지 파일 서비스 (정밀 감지 전략 사용)
   S3DetectionProperties detectionProps = new S3DetectionProperties();
   detectionProps.setMimeDetection(S3DetectionProperties.MimeDetection.PRECISE);
   MimeTypeDetectionStrategy mimeDetector = MimeTypeDetectionStrategyFactory.from(detectionProps);
   S3ImageService imageService = new S3ImageService(s3Client, "your-bucket-name", mimeDetector);
   ```

3. **파일 업로드**

   `S3FileService`의 `upload` 메서드를 사용하여 파일을 업로드합니다. 업로드 결과로 `S3UploadResult` 객체가 반환되며, 성공 시 S3 URL을 포함합니다.

   ```java
   File file = new File("path/to/your/file.txt");
   S3UploadResult result = fileService.upload("my-folder/my-file.txt", file);

   if (result.isSuccess()) {
       System.out.println("업로드 성공 URL: " + result.getUrl());
   } else {
       System.err.println("업로드 실패: " + result.getResponse().getErrorMessage());
       throw new RuntimeException("파일 업로드에 실패했습니다.");
   }
   ```

4. **이미지 업로드**

   `S3ImageService`를 사용하여 이미지를 업로드합니다. 이 서비스는 파일이 유효한 이미지인지 확장자와 MIME 타입을 검사하여 안정성을 높입니다.

   ```java
   File imageFile = new File("path/to/your/image.png");
   // S3ImageService 인스턴스는 '2. 서비스 인스턴스 생성' 부분을 참고하세요.
   S3UploadResult result = imageService.upload("my-folder/my-image.png", imageFile);

   if (result.isSuccess()) {
       System.out.println("이미지 업로드 성공 URL: " + result.getUrl());
   } else {
       System.err.println("이미지 업로드 실패: " + result.getResponse().getErrorMessage());
       // InvalidImageException 등 관련 예외 처리
       throw new RuntimeException("이미지 업로드에 실패했습니다.");
   }
   ```

### 4.2. Spring Framework 통합 예시

`@Configuration` 클래스를 사용하여 S3 관련 빈(Bean)을 등록하면 편리하게 주입받아 사용할 수 있습니다.

```java
@Configuration
public class S3Config {

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Bean
    public S3AccessConfig s3AccessConfig() {
        return S3AccessConfig.builder()
                .withCredentials(accessKey, secretKey)
                .withRegion(region)
                .build();
    }

    @Bean
    public S3Client s3Client(S3AccessConfig s3AccessConfig) {
        return S3ClientFactory.getS3Client(s3AccessConfig);
    }

    @Bean
    public S3FileService s3FileService(S3Client s3Client) {
        return new S3FileService(s3Client, bucket);
    }
    
    @Bean
    public S3ImageService s3ImageService(S3Client s3Client) {
        return new S3ImageService(s3Client, bucket, createPreciseMimeTypeDetector());
    }
    
    // Default: FastMineTypeDetector
    private MimeTypeDetectionStrategy createFastMimeTypeDetector() {
        S3DetectionProperties detectionProps = new S3DetectionProperties();                                                     │
        return MimeTypeDetectionStrategyFactory.from(detectionProps);
    }
    
    // Set to PreciseMimeTypeDetector
    private MimeTypeDectionStrategy createPreciseMimeTypeDetector() {
        S3DetectionProperties detectionProps = new S3DetectionProperties();
        detectionProps.setMimeDetection(S3DetectionProperties.MimeDetection.PRECISE);
        return MimeTypeDetectionStrategyFactory.from(detectionProps);
    }
}
    
    
```

```java
@Service
@RequiredArgsConstructor
public class MyFileService {

    private final S3FileService s3FileService;
    private final S3ImageService s3ImageService;

    public String uploadProfileImage(MultipartFile imageFile) throws RuntimeException {
        String key = "profiles/" + UUID.randomUUID() + ".jpg";
        S3UploadResult result = s3ImageService.upload(
            key,
            imageFile.getInputStream(),
            imageFile.getContentType(),
            imageFile.getSize()
        );

        if (result.isSuccess()) {
            return result.getUrl();
        }
        throw new RuntimeException("이미지 업로드 실패");
    }
}
```

## 5. 유효성 검사 규칙 및 예외 처리

본 라이브러리는 안정적인 S3 상호작용을 위해 각 데이터 모델에 대한 엄격한 유효성 검사를 수행합니다. 규칙에 위배될 경우, 명확한 예외가 발생합니다.

### 5.1. 데이터 모델별 유효성 검사 규칙

#### AccessKey (`InvalidAccessKeyException`)
- `null` 또는 비어 있을 수 없습니다.
- 길이는 16자 이상 128자 이하여야 합니다.
- 대문자 알파벳과 숫자로만 구성되어야 합니다.
- `AKIA` 또는 `ASIA`로 시작해야 합니다.

#### SecretKey (`InvalidSecretKeyException`)
- `null` 또는 비어 있을 수 없습니다.

#### Bucket (`InvalidBucketNameException`)
- `null` 또는 비어 있을 수 없습니다.
- 길이는 3자 이상 63자 이하여야 합니다.
- 소문자, 숫자, 하이픈(-), 점(.)만 포함할 수 있습니다.
- 문자 또는 숫자로 시작하고 끝나야 합니다.
- 연속된 점(..)을 포함할 수 없습니다.
- IP 주소 형식일 수 없습니다.
- 금지된 접두사(`xn--`, `sthree-` 등)로 시작할 수 없습니다.
- 금지된 접미사(`-s3alias`, `--ol-s3` 등)로 끝날 수 없습니다.

#### Key (`InvalidKeyException`)
- `null` 또는 비어 있을 수 없습니다.
- 길이는 1024자를 초과할 수 없습니다.
- 금지된 문자(`\:*?"<>|`)를 포함할 수 없습니다.
- 금지된 경로 시퀀스(`//`, `./`, `/.`)를 포함할 수 없습니다.
- `/` 또는 `\`로 시작할 수 없습니다.
- `.` 또는 `..`일 수 없습니다.

#### ContentType (`InvalidContentTypeException`)
- `null` 또는 비어 있을 수 없습니다.
- 유효한 MIME 타입 형식(`type/subtype`)이어야 합니다.
- `S3ImageService` 사용 시, 허용된 이미지 타입(`image/jpeg`, `image/png` 등)이어야 합니다.

#### ContentLength (`InvalidContentLengthException`)
- 0보다 커야 합니다.

### 5.2. 주요 예외 클래스 및 처리

유효성 검사 실패 외에도 다음과 같은 예외가 발생할 수 있습니다.

| 예외 클래스                       | 설명                                                                        |
| --------------------------------- | --------------------------------------------------------------------------- |
| `AwsCredentialsNotFoundException` | AWS 자격 증명을 찾을 수 없을 때 발생합니다.                                 |
| `AwsRegionNotFoundException`      | 유효하지 않은 AWS 리전을 설정했을 때 발생합니다.                            |
| `InvalidFileException`            | `File` 객체가 유효하지 않을 때 발생합니다. (예: 존재하지 않음, 디렉토리)      |
| `S3UploaderException`             | S3 업로드 중 AWS SDK 내부 오류 발생 시 발생합니다.                          |
| `AWSBucketNotFoundException`      | 작업을 요청한 버킷을 찾을 수 없을 때 발생합니다.                            |

**처리 예시:**

```java
try {
    S3UploadResult result = fileService.upload("test/my-file.txt", file);
    // ... 성공 로직
} catch (InvalidKeyException e) {
    log.error("S3 Key가 유효하지 않습니다: {}", e.getMessage());
} catch (InvalidFileException e) {
    log.error("파일이 유효하지 않습니다: {}", e.getMessage());
} catch (S3UploaderException e) {
    log.error("S3 업로드 중 오류가 발생했습니다: {}", e.getMessage());
} catch (Exception e) {
    log.error("알 수 없는 오류가 발생했습니다.", e);
}
```

## 6. 프로젝트 구조


```
src/main/java/org/websoso/s3/
├── config/         # S3 접속 정보 및 MIME 타입 감지 전략 설정
├── core/           # S3 핵심 서비스 (업로드, 조회, 삭제) 및 전략 인터페이스
│   └── strategy/   # MIME 타입 감지 전략 구현체
├── exception/      # 커스텀 예외 클래스
├── factory/        # S3 클라이언트 및 전략 객체 생성 팩토리
└── modle/          # S3 관련 데이터 모델 (Key, Bucket, 응답 객체 등)
```

## 7. 빌드 및 테스트

### 빌드

프로젝트 루트 디렉토리에서 다음 명령어를 실행하여 프로젝트를 빌드할 수 있습니다.

```bash
./gradlew build
```

### 테스트

다음 명령어를 통해 유닛 테스트를 실행할 수 있습니다.

```bash
./gradlew test
```