# JWT 토큰 테스트 가이드

## 🎯 JWT 구현 완료!

실제 JWT 토큰 생성, 검증, 재발급 기능이 모두 구현되었습니다.

---

## 1단계: 서버 실행

IntelliJ에서 `BoardBuddiesServerApplication` 실행

---

## 2단계: 소셜 로그인 (JWT 토큰 발급)

### 요청
```bash
curl -X POST http://localhost:8080/api/auth/social/kakao \
  -H "Authorization: Bearer {실제_카카오_액세스_토큰}"
```

### 응답 (기존 회원)
```json
{
  "code": 201,
  "message": "로그인 성공",
  "data": {
    "type": "Login",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTY...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE2..."
  }
}
```

**이제 진짜 JWT 토큰입니다!** 🎉

---

## 3단계: 발급받은 토큰으로 인증 API 호출

### 내 정보 조회 (JWT 인증 필요)

**요청:**
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer {위에서_받은_accessToken}"
```

**성공 응답:**
```json
{
  "code": 200,
  "message": "내 정보 조회 성공",
  "data": {
    "id": 1,
    "socialProvider": "KAKAO",
    "email": "user@example.com",
    "name": "홍길동",
    "school": "서울대학교",
    ...
  }
}
```

**토큰 없이 요청하면:**
```json
{
  "code": 401,
  "message": "인증이 필요합니다."
}
```

---

## 4단계: 액세스 토큰 만료 시 재발급

액세스 토큰이 만료되면 (1시간 후) 리프레시 토큰으로 재발급 받습니다.

### 요청
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer {refreshToken}"
```

### 응답
```json
{
  "code": 200,
  "message": "토큰 재발급 성공",
  "data": {
    "type": "Login",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.새로운_액세스_토큰...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.새로운_리프레시_토큰..."
  }
}
```

---

## 5단계: 회원가입 플로우 (신규 회원)

### 5-1. 소셜 로그인 (처음)

**요청:**
```bash
curl -X POST http://localhost:8080/api/auth/social/kakao \
  -H "Authorization: Bearer {카카오_액세스_토큰}"
```

**응답:**
```json
{
  "code": 201,
  "message": "소셜 로그인 성공. 추가 정보를 입력해주세요.",
  "data": {
    "type": "Signup",
    "tempAccessToken": "eyJhbGciOiJIUzI1NiJ9.임시_토큰...",
    "provider": "KAKAO",
    "email": "user@example.com"
  }
}
```

**임시 토큰 특징:**
- 유효기간: 30분
- 회원가입 완료 API만 호출 가능

### 5-2. 추가 정보 입력 (회원가입 완료)

**요청:**
```bash
curl -X POST http://localhost:8080/api/auth/signup/complete \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {위에서_받은_tempAccessToken}" \
  -d '{
    "name": "홍길동",
    "birthDate": "2000-01-01",
    "school": "서울대학교",
    "studentId": "2020123456",
    "gender": "MALE",
    "phoneNumber": "010-1234-5678"
  }'
```

**응답:**
```json
{
  "code": 201,
  "message": "회원가입 성공",
  "data": {
    "type": "Signup",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.정식_액세스_토큰...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.정식_리프레시_토큰..."
  }
}
```

이제 정식 토큰으로 모든 API 사용 가능!

---

## JWT 토큰 구조 확인

[jwt.io](https://jwt.io)에서 토큰을 디코딩해서 내용을 확인할 수 있습니다.

**액세스 토큰 payload:**
```json
{
  "sub": "1",           // 사용자 ID
  "type": "access",     // 토큰 타입
  "iat": 1234567890,    // 발급 시간
  "exp": 1234571490     // 만료 시간 (1시간 후)
}
```

**리프레시 토큰 payload:**
```json
{
  "sub": "1",
  "type": "refresh",
  "iat": 1234567890,
  "exp": 1235172690     // 만료 시간 (7일 후)
}
```

---

## 에러 케이스 테스트

### 1. 만료된 토큰
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer {만료된_토큰}"
```
응답: `401 만료된 토큰입니다.`

### 2. 잘못된 토큰
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer invalid-token"
```
응답: `401 유효하지 않은 토큰입니다.`

### 3. 토큰 없이 요청
```bash
curl -X GET http://localhost:8080/api/users/me
```
응답: 인증 실패 (Spring Security가 처리)

---

## 프론트엔드 개발자를 위한 가이드

### 로그인 플로우

```javascript
// 1. 카카오 로그인으로 카카오 토큰 받기
const kakaoToken = await getKakaoAccessToken();

// 2. 백엔드로 소셜 로그인 요청
const response = await fetch('/api/auth/social/kakao', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${kakaoToken}`
  }
});

const result = await response.json();

if (result.data.type === 'Login') {
  // 기존 회원 - 바로 로그인
  localStorage.setItem('accessToken', result.data.accessToken);
  localStorage.setItem('refreshToken', result.data.refreshToken);
  navigate('/home');
  
} else if (result.data.type === 'Signup') {
  // 신규 회원 - 추가 정보 입력 필요
  localStorage.setItem('tempToken', result.data.tempAccessToken);
  navigate('/signup/complete');
}
```

### 인증 API 호출

```javascript
// 액세스 토큰으로 API 호출
const accessToken = localStorage.getItem('accessToken');

const response = await fetch('/api/users/me', {
  headers: {
    'Authorization': `Bearer ${accessToken}`
  }
});

if (response.status === 401) {
  // 토큰 만료 - 재발급
  await refreshToken();
}
```

### 토큰 재발급

```javascript
async function refreshToken() {
  const refreshToken = localStorage.getItem('refreshToken');
  
  const response = await fetch('/api/auth/refresh', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${refreshToken}`
    }
  });
  
  if (response.ok) {
    const result = await response.json();
    localStorage.setItem('accessToken', result.data.accessToken);
    localStorage.setItem('refreshToken', result.data.refreshToken);
  } else {
    // 리프레시 토큰도 만료 - 다시 로그인
    localStorage.clear();
    navigate('/login');
  }
}
```

---

## 다른 API에서 현재 사용자 정보 사용하기

### 컨트롤러 예시

```java
@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    @PostMapping
    public ResponseEntity<?> createPost(
        @CurrentUser Long userId,  // 🔥 현재 로그인한 사용자 ID
        @RequestBody CreatePostRequest request) {
        
        // userId로 게시글 작성
        Post post = postService.createPost(userId, request);
        return ResponseEntity.ok(post);
    }
}
```

**@CurrentUser 애노테이션이 자동으로 JWT에서 userId를 추출합니다!**

---

## 설정 정보

### JWT 설정 (application.yaml)

```yaml
jwt:
  secret: "최소 32자 이상의 비밀키"
  access-token-expiration: 3600000    # 1시간
  refresh-token-expiration: 604800000 # 7일
```

**프로덕션 환경에서는 반드시 환경변수로 설정하세요:**
```bash
export JWT_SECRET="your-super-secret-key-change-this-in-production-min-32-chars"
```

---

## 완료! 🎉

이제 JWT 기반 인증이 완벽하게 구현되었습니다:
- ✅ 실제 JWT 토큰 생성
- ✅ 토큰 검증 (만료, 서명 확인)
- ✅ 토큰 재발급
- ✅ 인증 필터 (모든 요청 자동 검증)
- ✅ @CurrentUser 애노테이션 (편리한 사용자 정보 추출)

다른 기능 개발 시 `@CurrentUser Long userId`만 추가하면 현재 로그인한 사용자로 작업할 수 있습니다!

