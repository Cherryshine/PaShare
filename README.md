# PaShare (간편 텍스트 공유 서비스)

PaShare는 텍스트를 간편하게 공유할 수 있는 웹 애플리케이션입니다. 사용자는 텍스트를 작성하고 4자리 숫자 코드를 통해 다른 사용자와 공유하거나, 링크를 통해 24시간 동안 유효한 텍스트를 공유할 수 있습니다.

## 주요 기능

- 텍스트 저장 및 4자리 숫자코드를 통한 공유
- 링크를 통한 텍스트 공유 (24시간 유효)
- 관리자 패널을 통한 텍스트 관리
- Cloudflare Turnstile을 활용한 CAPTCHA 검증

## 기술 스택

- Backend: Spring Boot 3.x, Spring Data JPA
- Frontend: Thymeleaf, HTML/CSS, JavaScript
- Database: MySQL
- 보안: JWT, Cloudflare Turnstile

## 설치 및 실행 방법

### 사전 요구사항

- JDK 17 이상
- MySQL 8.0 이상
- Gradle 7.x 이상

### 환경 설정

1. 프로젝트 복제
```bash
git clone https://github.com/yourusername/pashare.git
cd pashare
```

2. 데이터베이스 설정
```sql
CREATE DATABASE text;
```

3. 애플리케이션 설정
- `src/main/resources/application-sample.properties` 파일을 `application.properties`로 복사합니다.
- 데이터베이스 접속 정보, 관리자 비밀번호, Cloudflare Turnstile 키 등을 설정합니다.

```properties
# 데이터베이스 연결 설정
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# 관리자 비밀번호 설정
admin.password=your_admin_password

# Cloudflare Turnstile CAPTCHA 설정
turnstile.site-key=your_site_key
turnstile.secret-key=your_secret_key
```

### 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 빌드 후 실행:

```bash
./gradlew build
java -jar build/libs/Pashare-0.0.1-SNAPSHOT.war
```

## 보안 설정

이 프로젝트는 민감한 정보를 포함하고 있습니다. 다음 파일들은 git 저장소에 포함되지 않도록 `.gitignore`에 설정되어 있습니다:

- `application.properties` (실제 운영 설정)
- `application-dev.properties` (개발 환경 설정)
- `application-prod.properties` (운영 환경 설정)

## API 엔드포인트

### 텍스트 API
- `POST /api/save-text`: 텍스트 저장
- `GET /api/get-text?code={code}`: 코드로 텍스트 조회
- `POST /api/share-via-link`: 링크로 텍스트 공유
- `GET /api/s/{id}`: 공유된 텍스트 조회

### 관리자 API
- `POST /api/admin/login`: 관리자 로그인
- `GET /api/admin/texts`: 모든 텍스트 목록 조회
- `DELETE /api/admin/texts/{index}`: 특정 텍스트 삭제
- `DELETE /api/admin/texts`: 모든 텍스트 삭제

### CAPTCHA API
- `POST /api/verify-captcha`: Turnstile CAPTCHA 검증

## 기여 방법

1. 이 저장소를 포크합니다.
2. 새 기능 브랜치를 생성합니다. (`git checkout -b feature/amazing-feature`)
3. 변경사항을 커밋합니다. (`git commit -m 'Add some amazing feature'`)
4. 브랜치에 푸시합니다. (`git push origin feature/amazing-feature`)
5. Pull Request를 제출합니다.

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 