# BoardBuddies Server 테스트 가이드

## 1단계: 애플리케이션 실행

### 방법 1: Gradle로 실행 (권장)
```bash
cd /Users/iyeonsu/Documents/GitHub/BoardBuddies/server
./gradlew bootRun
```

### 방법 2: IDE에서 실행
IntelliJ에서 `BoardBuddiesServerApplication.java` 파일을 열고 Run 버튼 클릭

### 실행 확인
서버가 정상 실행되면 콘솔에 다음과 같은 메시지가 표시됩니다:
```
Started BoardBuddiesServerApplication in X.XXX seconds
```

기본 포트: `http://localhost:8080`

---

## 2단계: H2 데이터베이스 콘솔 확인

브라우저에서 H2 콘솔 접속:
```
http://localhost:8080/h2-console
```

로그인 정보:
- JDBC URL: `jdbc:h2:mem:testdb`
- User Name: `sa`
- Password: (비워두기)

연결 후 `USERS` 테이블이 자동 생성되었는지 확인:
```sql
SELECT * FROM USERS;
```

---

## 3단계: API 테스트

### 3-1. 헬스 체크 (서버 작동 확인)

**요청:**
```bash
curl http://localhost:8080/api/test/health
```

**예상 응답:**
```json
{
  "code": 200,
  "message": "서버가 정상 작동 중입니다.",
  "data": "OK"
}
```

---

### 3-2. 소셜 로그인 API 테스트 (실제 토큰 필요)

#### 카카오 로그인 테스트

**요청:**
```bash
curl -X POST http://localhost:8080/api/auth/social/kakao \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {실제_카카오_액세스_토큰}"
```

**신규 회원 예상 응답:**
```json
{
  "code": 201,
  "message": "소셜 로그인 성공. 추가 정보를 입력해주세요.",
  "data": {
    "type": "Signup",
    "tempAccessToken": "temp-token-for-signup-1",
    "provider": "KAKAO",
    "email": "user@example.com"
  }
}
```

**기존 회원 예상 응답:**
```json
{
  "code": 201,
  "message": "로그인 성공",
  "data": {
    "type": "Login",
    "accessToken": "generated-access-token-for-user-1",
    "refreshToken": "generated-refresh-token-for-user-1"
  }
}
```

---

#### 네이버 로그인 테스트

**요청:**
```bash
curl -X POST http://localhost:8080/api/auth/social/naver \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {실제_네이버_액세스_토큰}"
```

응답은 카카오와 동일한 형식

---

### 3-3. 회원가입 완료 API 테스트

신규 회원이 추가 정보를 입력하는 단계입니다.

**요청:**
```bash
curl -X POST http://localhost:8080/api/auth/signup/complete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {tempAccessToken}" \
  -d '{
    "name": "홍길동",
    "birthDate": "2000-01-01",
    "school": "서울대학교",
    "studentId": "2020123456",
    "gender": "MALE",
    "phoneNumber": "010-1234-5678"
  }'
```

**예상 응답:**
```json
{
  "code": 201,
  "message": "회원가입 성공",
  "data": {
    "type": "Signup",
    "accessToken": "generated-access-token-for-user-1",
    "refreshToken": "generated-refresh-token-for-user-1"
  }
}
```

---

### 3-4. 에러 케이스 테스트

#### 토큰 없이 요청
```bash
curl -X POST http://localhost:8080/api/auth/social/kakao \
  -H "Content-Type: application/json"
```

**예상 응답:**
```json
{
  "code": 400,
  "message": "토큰이 없습니다.",
  "data": null
}
```

#### 잘못된 토큰 형식
```bash
curl -X POST http://localhost:8080/api/auth/social/kakao \
  -H "Content-Type: application/json" \
  -H "Authorization: InvalidToken"
```

**예상 응답:**
```json
{
  "code": 401,
  "message": "유효하지 않은 토큰입니다.",
  "data": null
}
```

#### 유효하지 않은 소셜 제공자
```bash
curl -X POST http://localhost:8080/api/auth/social/google \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer some-token"
```

**예상 응답:**
```json
{
  "code": 400,
  "message": "지원하지 않는 소셜 제공자입니다.",
  "data": null
}
```

---

## 4단계: Postman으로 테스트 (권장)

### Postman 컬렉션 설정

1. **헬스 체크**
   - Method: `GET`
   - URL: `http://localhost:8080/api/test/health`

2. **소셜 로그인 (카카오)**
   - Method: `POST`
   - URL: `http://localhost:8080/api/auth/social/kakao`
   - Headers:
     - `Content-Type`: `application/json`
     - `Authorization`: `Bearer {카카오_액세스_토큰}`

3. **회원가입 완료**
   - Method: `POST`
   - URL: `http://localhost:8080/api/auth/signup/complete`
   - Headers:
     - `Content-Type`: `application/json`
     - `Authorization`: `Bearer {tempAccessToken}`
   - Body (raw JSON):
     ```json
     {
       "name": "홍길동",
       "birthDate": "2000-01-01",
       "school": "서울대학교",
       "studentId": "2020123456",
       "gender": "MALE",
       "phoneNumber": "010-1234-5678"
     }
     ```

---

## 5단계: 실제 소셜 토큰 얻기

### 카카오 액세스 토큰 얻는 방법

1. [Kakao Developers](https://developers.kakao.com/)에서 애플리케이션 등록
2. REST API 키 확인
3. 다음 URL로 브라우저에서 접속 (REST API 키 입력):
   ```
   https://kauth.kakao.com/oauth/authorize?client_id={REST_API_KEY}&redirect_uri=http://localhost:8080&response_type=code
   ```
4. 로그인 후 리다이렉트된 URL에서 `code` 파라미터 복사
5. 다음 curl 명령으로 액세스 토큰 얻기:
   ```bash
   curl -X POST https://kauth.kakao.com/oauth/token \
     -d "grant_type=authorization_code" \
     -d "client_id={REST_API_KEY}" \
     -d "redirect_uri=http://localhost:8080" \
     -d "code={위에서_얻은_코드}"
   ```

### 네이버 액세스 토큰 얻는 방법

1. [NAVER Developers](https://developers.naver.com/)에서 애플리케이션 등록
2. Client ID, Client Secret 확인
3. 네이버 로그인 API 문서 참고하여 토큰 획득

---

## 6단계: 로그 확인

애플리케이션 실행 중 콘솔에서 로그를 확인하세요:

- `신규 회원 소셜 로그인`: 처음 로그인하는 사용자
- `기존 회원 로그인`: 이미 가입된 사용자
- `회원가입 완료`: 추가 정보 입력 완료

에러 발생 시 상세한 스택 트레이스가 출력됩니다.

---

## 주의사항

⚠️ **현재 구현 상태:**
- JWT 토큰은 임시 문자열입니다 (실제 JWT 생성 필요)
- 소셜 토큰 검증은 실제 API 호출로 수행됩니다
- 데이터는 H2 메모리 DB에 저장됩니다 (재시작 시 초기화)

✅ **다음 단계:**
1. JWT 라이브러리 추가 및 실제 토큰 생성 구현
2. MySQL/PostgreSQL 등 실제 DB 연결
3. 소셜 로그인 Client ID/Secret 환경변수 설정
4. CORS 설정 (프론트엔드 연동 시)

