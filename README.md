# WSS S3 Service

# 1. 개요

AWS SDK for Java V2를 기반으로, AWS S3(Simple Storage Service)와의 상호작용을 간소화하고 파일 및 이미지 업로드/조회/삭제 기능을 안정적이고 편리하게 사용할 수 있도록 설계된 Java 라이브러리입니다.

본 라이브러리는 S3의 핵심 기능들을 추상화하여 제공하며, 특히 이미지 파일의 MIME 타입 감지, 콘텐츠 크기 검증 등과 같은 부가적인 기능들을 포함하여 개발자가 비즈니스 로직에 더 집중할 수 있도록 돕습니다.

# 2. 주요 기능

- **간편한 파일 및 이미지 관리**: `S3FileService`와 `S3ImageService`를 통해 인터페이스`S3DefaultService`로 파일을 업로드, 조회, 삭제할 수 있습니다.
- **유연한 업로드 방식**: `File` 객체와 `InputStream`을 모두 지원하여 다양한 환경에 적용할 수 있습니다.
- **안전한 이미지 업로드 (MIME 타입 검증)**: `S3ImageService`를 통해 이미지 업로드 시, 파일의 유효성을 검증하기 위해 MIME 타입을 감지합니다.
    - **고속(FAST) 전략** : 파일 헤더 일부를 분석하여 검사합니다. 빠르지만 의도적인 조작을 탐지하기는 어렵습니다. (tika-core 기반의 감지 방식)
    - **정밀(PRECISE) 전략** : 파일 내부 시그니처를 분석하여 검사합니다. 정확도는 높지만, 환경에 따라 메모리 및 디스크 I/O 오버헤드가 발생할 수 있습니다. (tika-parsers 기반의 분석 방식)
- **강타입(Strongly-typed) 모델**: `ObjectKey`, `Bucket`, `AccessKey` 등 S3의 주요 개념들을 클래스로 래핑하여 컴파일 타임에 실수를 방지하고 코드의 안정성을 높입니다.
- **체계적인 예외 처리 계층**: AWS SDK의 복잡한 예외를 추상화하여, 목적에 따라 세 가지 최상위 예외로 명확하게 구분했습니다. 하위에는 세분화된 커스텀 예외를 제공합니다. 이를 통해 라이브러리 사용자는 오류를 일관되고 손쉽게 처리할 수 있습니다.
   - **ValidationException**: 사용자 입력값(파일 형식 등)이 규칙에 맞지 않을 때 발생합니다.
  - **AwsGlobalException**: AWS 전역 설정(자격 증명, 리전 설정 등)이 잘못되었을 때 발생합니다. 
  - **S3OperationException**: S3 서비스 사용 중 발생하는 모든 오류(버킷 없음, 접근 거부 등)를 처리합니다.
- **유연한 설정**: `S3AccessConfig`를 통해 AWS 자격 증명(Credentials) 및 리전(Region)을 쉽게 설정할 수 있습니다. 환경 변수, 시스템 프로퍼티를 통한 자동 설정도 지원합니다.
- **팩토리 및 캐싱**: `S3ClientFactory`와 `MimeTypeDetectionStrategyFactory`를 통해 S3 클라이언트와 MIME 타입 감지 전략을 유연하게 생성하고, 생성된 S3 클라이언트를 캐싱하여 성능을 최적화합니다.

# 3. 설치 방법

## Gradle (JitPack)

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Team-WSS:WSS-Server-S3:{latest-version}'
}
```

## Maven (JitPack)

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

# 4. 사용 방법

## 4.1. 기본 사용법

### 4.1.1. **S3 클라이언트 설정**

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

### 4.1.2. **서비스 인스턴스 생성**
- **일반 파일용**: `S3FileService`
- **이미지 파일용**: `S3ImageService` (MIME 타입 감지 전략 지정 가능 / 미지정시 기본값은 FAST 전략)
    - 고속(FAST) 전략 : 파일 헤더 일부를 분석하여 검사합니다. 빠르지만 의도적인 조작을 탐지하기는 어렵습니다. (tika-core 기반의 감지 방식)
    - 정밀(PRECISE) 전략 : 파일 내부 시그니처를 분석하여 검사합니다. 정확도는 높지만, 환경에 따라 메모리 및 디스크 I/O 오버헤드가 발생할 수 있습니다. (tika-parsers 기반의 분석 방식)

```java
// 일반 파일 서비스
S3FileService fileService = new S3FileService(s3Client, "your-bucket-name");

// 이미지 서비스 (기본: FAST 전략)
S3ImageService defaultImageService = new S3ImageService(s3Client, "your-bucket-name");

// 이미지 서비스 (지정: FAST 전략)
S3DetectionProperties fastProps = new S3DetectionProperties();
fastProps.setMimeDetection(S3DetectionProperties.MimeDetection.FAST);
MimeTypeDetectionStrategy fastDetector = MimeTypeDetectionStrategyFactory.from(fastProps);
S3ImageService fastImageService = new S3ImageService(s3Client, "your-bucket-name", fastDetector);

// 이미지 서비스 (지정: PRECISE 전략)
S3DetectionProperties preciseProps = new S3DetectionProperties();
preciseProps.setMimeDetection(S3DetectionProperties.MimeDetection.PRECISE);
MimeTypeDetectionStrategy preciseDetector = MimeTypeDetectionStrategyFactory.from(preciseProps);
S3ImageService preciseImageService = new S3ImageService(s3Client, "your-bucket-name", preciseDetector);
```

### 4.1.3. **파일 업로드**

`S3FileService`의 `upload` 메서드를 사용하여 파일을 업로드합니다. 업로드 결과로 `S3UploadResult` 객체가 반환되며, 성공 시 S3 URL을 포함합니다.

```java
File file = new File("path/to/your/file.txt");
String objectKey = "my-folder/my-file.txt";

try {
  S3UploadResult result = fileService.upload(objectKey, file);
  System.out.println("업로드 성공 URL: " + result.getUrl());
} catch (ValidationException e) {
  log.error("입력값이 유효하지 않습니다: {}", e.getMessage());
} catch (AwsGlobalException e) {
  log.error("AWS 설정 오류가 발생했습니다: {}", e.getMessage());
} catch (S3OperationException e) {
  log.error("S3 작업 중 오류 발생 (HTTP: {}, Code: {}): {}", e.getHttpStatusCode(), e.getAwsErrorCode(), e.getMessage());
} catch (Exception e) {
log.error("알 수 없는 오류가 발생했습니다.", e);
}
```
> 구체적인 예외 처리 가이드는 '5. 예외 처리 가이드' 부분을 참고하세요.


### 4.1.4. **이미지 업로드**

`S3ImageService`를 사용하여 이미지를 업로드합니다. S3ImageService 인스턴스 생성 시 지정한 감지 전략에 따라 유효한 이미지인지 검사하여 안정성을 높입니다.

```java
File imageFile = new File("path/to/your/image.png");
String objectKey = "my-images/my-avatar.png";

try {
  S3UploadResult result = imageService.upload(objectKey, imageFile);
  System.out.println("이미지 업로드 성공 URL: " + result.getUrl());
} catch (ValidationException e) {
  log.error("입력값이 유효하지 않습니다: {}", e.getMessage());
} catch (AwsGlobalException e) {
  log.error("AWS 설정 오류가 발생했습니다: {}", e.getMessage());
} catch (S3OperationException e) {
  log.error("S3 작업 중 오류 발생 (HTTP: {}, Code: {}): {}", e.getHttpStatusCode(), e.getAwsErrorCode(), e.getMessage());
} catch (Exception e) {
log.error("알 수 없는 오류가 발생했습니다.", e);
}
```
> 구체적인 예외 처리 가이드는 '5. 예외 처리 가이드' 부분을 참고하세요.

> S3ImageService 인스턴스 생성 가이드는 '4.1.2. 서비스 인스턴스 생성' 부분을 참고하세요.



## 4.2. Spring Framework 통합 예시

`@Configuration` 클래스를 사용하여 S3 관련 빈(Bean)을 등록하고, @Service에서 편리하게 주입받아 사용할 수 있습니다.


### 4.2.1. S3 관련 Bean 설정 (S3Config.java)
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

    // S3AccessConfig Bean 설정
    @Bean
    public S3AccessConfig s3AccessConfig() {
        return S3AccessConfig.builder()
                .withCredentials(accessKey, secretKey)
                .withRegion(region)
                .build();
    }

    // S3Client Bean 설정
    @Bean
    public S3Client s3Client(S3AccessConfig s3AccessConfig) {
        return S3ClientFactory.getS3Client(s3AccessConfig);
    }
    
    // S3FileService Bean 설정
    @Bean
    public S3FileService s3FileService(S3Client s3Client) {
        return new S3FileService(s3Client, bucket);
    }

    // S3ImageService Bean 설정 (아래 두 가지 방법 중 하나를 선택, 둘 다 사용시 @Primary 명시)
    // 방법 1: 정확성이 중요한 경우 (PRECISE 전략)
    @Bean
    @Primary // 여러 S3ImageService Bean 중 우선권을 가짐
    public S3ImageService s3ImageServicePrecise(S3Client s3Client) {
        S3DetectionProperties preciseProps = new S3DetectionProperties();
        preciseProps.setMimeDetection(S3DetectionProperties.MimeDetection.PRECISE);
        MimeTypeDetectionStrategy preciseDetector = MimeTypeDetectionStrategyFactory.from(preciseProps);
        return new S3ImageService(s3Client, bucket, preciseDetector);
    }
    // 방법 2: 속도가 중요한 경우 (FAST 전략 - 기본값)
    @Bean
    public S3ImageService s3ImageServiceFast(S3Client s3Client) {
        return new S3ImageService(s3Client, bucket);
    }
    
}
```
### 4.2.1. 서비스 레이어에서 사용 예시 (MyFileService.java)

```java
@Service
@RequiredArgsConstructor
public class MyFileService {
    
    private final S3FileService s3FileService;
    
    // 여러 개의 S3ImageService Bean을 등록한 경우, @Primary로 지정된 s3ImageServicePrecise Bean이 기본으로 주입됩니다.
    // 만약 다른 전략을 사용하고 싶다면 @Qualifier("s3ImageServiceFast")와 같이 사용하세요.
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

# 5. 예외 처리 가이드
WSS S3 Service는 안정적인 S3 상호작용을 위해 엄격한 입력값 검증 및 체계적인 예외 계층을 제공합니다.
모든 예외는 RuntimeException을 상속하며, 세 가지 최상위 예외 클래스로 구분됩니다.
이를 통해 사용자는 발생 가능한 오류를 일관성 있게 분류하고 상황에 맞게 처리할 수 있습니다.
- `ValidationException` : 입력값 검증 예외
- `AwsGlobalException` : AWS 전역 설정 예외
- `S3OperationException` : S3 작업 예외

## 5.1. 예외 계층 구조
```
RuntimeException
│   
├── ValidationException (입력값 검증 최상위 예외 객체)
│   └── InvalidAccessKeyException (AccessKey 입력값 예외 객체)
│   └── InvalidSecretKeyException (SecretKey 입력값 예외 객체)
│   └── InvalidBucketNameException (Bucket 입력값 예외 객체)
│   └── InvalidObjectKeyException (ObjectKey 입력값 예외 객체)
│   └── InvalidContentTypeException (ContentType 입력값 예외 객체)
│   └── InvalidContentLengthException (ContentLength 입력값 예외 객체)
│   └── InvalidFileException (File 입력값 예외 객체)
│   └── InvalidImageException (Image 입력값 예외 객체)
│   
├── AwsGlobalException (AWS 전역 설정 최상위 예외 객체)
│   └── AwsCredentialsNotFoundException (AWS 자격 증명 예외 객체)
│   └── AwsRegionNotFoundException (AWS 리전 예외 객체)
│   
└── S3OperationException (S3 작업 최상위 예외 객체)
    └── S3BucketNotFoundException (S3 버킷 예외 객체)
    └── S3ObjectKeyNotFoundException (S3 객체 키 예외 객체)
    └── S3AccessDeniedException (S3 권한 예외 객체)
    └── S3EntityTooLargeException (S3 업로드 파일 예외 객체)
```

## 5.1. 예외 종류
### 5.1.1. 입력값 검증 예외 (`ValidationException`)
라이브러리 사용자의 입력값이 사전에 정의된 규칙을 위반했을 때 발생합니다. S3에 요청을 보내기 전에 클라이언트 측에서 방지할 수 있는 오류입니다.

- `InvalidAccessKeyException` (AccessKey)
  - `null` 또는 비어 있을 수 없습니다.
  - 길이는 16자 이상 128자 이하여야 합니다.
  - 대문자 알파벳과 숫자로만 구성되어야 합니다.
  - `AKIA` 또는 `ASIA`로 시작해야 합니다.


- `InvalidSecretKeyException` (SecretKey)
  - `null` 또는 비어 있을 수 없습니다.


- `InvalidBucketNameException` (Bucket)
  - `null` 또는 비어 있을 수 없습니다.
  - 길이는 3자 이상 63자 이하여야 합니다.
  - 소문자, 숫자, 하이픈(-), 점(.)만 포함할 수 있습니다.
  - 문자 또는 숫자로 시작하고 끝나야 합니다.
  - 연속된 점(..)을 포함할 수 없습니다.
  - IP 주소 형식일 수 없습니다.
  - 금지된 접두사(`xn--`, `sthree-` 등)로 시작할 수 없습니다.
  - 금지된 접미사(`-s3alias`, `--ol-s3` 등)로 끝날 수 없습니다.


- `InvalidObjectKeyException` (ObjectKey)
  - `null` 또는 비어 있을 수 없습니다.
  - 길이는 1024자를 초과할 수 없습니다.
  - 금지된 문자(`\:*?"<>|`)를 포함할 수 없습니다.
  - 금지된 경로 시퀀스(`//`, `./`, `/.`)를 포함할 수 없습니다.
  - `/` 또는 `\`로 시작할 수 없습니다.
  - `.` 또는 `..`일 수 없습니다.


- `InvalidContentTypeException` (ContentType)
  - `null` 또는 비어 있을 수 없습니다.
  - 유효한 MIME 타입 형식(`type/subtype`)이어야 합니다.
  - `S3ImageService` 사용 시, 허용된 이미지 타입(`image/jpeg`, `image/png` 등)이어야 합니다.


- `InvalidContentLengthException` (ContentLength)
  - 0보다 커야 합니다.


- `InvalidFileException` (File)
  - File 객체가 null이거나, 존재하지 않거나, 디렉토리일 경우 발생합니다.


- `InvalidImageException` (Image)
  - S3ImageService 사용 시, 파일이 허용된 이미지 형식이 아닐 때 발생합니다.



### 5.1.2. AWS 전역 설정 예외 (`AwsGlobalException`)

S3 클라이언트를 설정하거나 AWS 자격 증명을 확인하는 단계에서 발생하는 오류입니다.

- `AwsCredentialsNotFoundException`
  - AWS 자격 증명을 찾을 수 없을 때 발생합니다.


- `AwsRegionNotFoundException`
  - 유효하지 않은 AWS 리전을 설정했을 때 발생합니다.


### 5.1.3. S3 작업 예외 (`S3OperationException`)
S3 서비스와 통신하는 과정에서 발생하는 모든 클라이언트 측, 서버 측 오류를 포함합니다.

- `S3BucketNotFoundException`
  - 작업을 요청한 버킷을 찾을 수 없을 때 발생합니다.


- `S3ObjectKeyNotFoundException`
  - 객체 키를 찾을 수 없을 때 발생합니다.


- `S3AccessDeniedException`
  - S3 서비스 요청에 대한 권한이 없을 때 발생합니다.


- `S3EntityTooLargeException`
  - 업로드하려는 파일이 너무 클 때 발생합니다.


`S3OperationException`은 자체적으로 `awsErrorCode`, `awsRequestId`, `awsExtendedRequestId`, `httpStatusCode` 와 같은 메타데이터를 제공하며, 사용자는 getter 메서드를 통해 해당 정보에 접근할 수 있습니다. 단, 오류 상황에 따라 `S3OperationException`이 포함하는 메타데이터의 제공 범위에는 차이가 있을 수 있습니다. 상세 내용은 다음의 표와 같습니다.

| 오류 유형                  | awsErrorCode | awsRequestId | awsExtendedRequestId | httpStatusCode |
|---------------------------|--------------|-------------|----------------------|----------------|
| **AWS S3 서비스 오류**      | 실제 값(String) | 실제 값(String) | 실제 값(String) | 실제 값(int)      |
| **그 외 AWS 서비스 오류**   | `null`       | 실제 값(String) | 실제 값(String) | 실제 값(int)      |
| **AWS 클라이언트 오류**     | `null`       | `null`| `null` | `-1`           |





## 5.2. 예외 처리 예시
```java
try {
    S3UploadResult result = fileService.upload("test/my-file.txt", file);
    // ... 성공 로직
    
} catch (InvalidImageException e) {
   // 유효성 검증 실패: 이미지 형식 실패를 별도로 처리
   log.error("업로드할 수 없는 이미지 파일입니다: {}", e.getMessage());
   
} catch (ValidationException e) {
   // 유효성 검증 실패: 그 외 유효성 검증 오류(객체 키 규칙 위반 등)를 포괄적으로 처리
   log.error("입력값이 유효하지 않습니다: {}", e.getMessage());
   
} catch (AwsRegionNotFoundException e) {
   // AWS 전역 설정 실패: 리전 설정 오류를 별도로 처리
   log.error("리전이 유효하지 않습니다: {}", e.getMessage());
   
} catch (AwsGlobalException e) {
   // AWS 전역 설정 실패: 그 외 AWS 전역 설정 오류(자격 증명 등)를 포괄적으로 처리
   log.error("AWS 설정 오류가 발생했습니다: {}", e.getMessage());
   
} catch (S3BucketNotFoundException e) {
   // S3 작업 실패: 특정 케이스(버킷 없음)를 별도로 처리
   log.error("S3 버킷 '{}'를 찾을 수 없습니다. (Request ID: {})", e.getBucketName(), e.getAwsRequestId());
   
} catch (S3OperationException e) {
   // S3 작업 실패: 그 외 S3 관련 오류(권한, 네트워크 등)를 포괄적으로 처리
   log.error("S3 작업 중 오류 발생 (HTTP: {}, Code: {}): {}", e.getHttpStatusCode(), e.getAwsErrorCode(), e.getMessage());
   
} catch (Exception e) {
    log.error("알 수 없는 오류가 발생했습니다.", e);
}
```


# 6. 프로젝트 구조

```
src/main/java/org/websoso/s3/
├── config/         # S3 접속 정보 및 MIME 타입 감지 전략 설정
├── core/           # S3 핵심 서비스 (업로드, 조회, 삭제) 및 전략 인터페이스
│   └── strategy/   # MIME 타입 감지 전략 구현체
├── exception/      # 커스텀 예외 클래스 (aws/s3, aws/global, validation)
├── factory/        # S3 클라이언트 및 전략 객체 생성 팩토리
└── modle/          # S3 관련 데이터 모델 (AccessKey, SecretKey, Bucket, 응답 객체 등)
```

# 7. 빌드 및 테스트

## 빌드

프로젝트 루트 디렉토리에서 다음 명령어를 실행하여 프로젝트를 빌드할 수 있습니다.

```bash
./gradlew build
```

## 테스트

다음 명령어를 통해 유닛 테스트를 실행할 수 있습니다.

```bash
./gradlew test
```