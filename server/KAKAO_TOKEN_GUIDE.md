# 카카오 액세스 토큰 받기

## 🎯 방법 1: Kakao REST API 테스트 도구 (가장 간단!)

### 1단계: 카카오 개발자 콘솔 설정

1. [카카오 개발자 콘솔](https://developers.kakao.com) 접속
2. 내 애플리케이션 → 본인의 앱 선택
3. 왼쪽 메뉴 `제품 설정` → `카카오 로그인` 클릭
4. **활성화 설정**을 ON으로 변경
5. **Redirect URI 등록**:
   - `http://localhost:8080` 추가
   - 저장

### 2단계: 간편 토큰 발급 (개발용)

**방법 A: 카카오 개발자 도구 사용**

1. 카카오 개발자 콘솔에서 `도구` 메뉴 선택
2. `REST API 테스트` 클릭
3. 왼쪽에서 `사용자 관리` → `사용자 정보 가져오기` 선택
4. `Access Token 발급` 버튼 클릭
5. 카카오 계정으로 로그인
6. **액세스 토큰 복사!** ← 이걸 사용하면 됩니다

**이 토큰의 유효기간: 2시간**

---

## 🎯 방법 2: OAuth 플로우로 직접 받기

### 1단계: 인가 코드 받기

브라우저에서 다음 URL 접속 (REST API 키를 본인 키로 변경):

```
https://kauth.kakao.com/oauth/authorize?client_id=f2a941eeb74d1c0cb0d136fbdacad0a1&redirect_uri=http://localhost:8080&response_type=code
```

**주의:** `client_id=` 뒤에 본인의 REST API 키 입력!

### 2단계: 리다이렉트된 URL에서 code 추출

로그인하면 다음과 같은 URL로 리다이렉트됩니다:
```
http://localhost:8080/?code=xxxxxxxxxxxxxxxxxxxxxxxxxxx
```

`code=` 뒤의 값을 복사하세요!

### 3단계: 액세스 토큰 받기

터미널에서 다음 명령 실행 (본인의 값으로 변경):

```bash
curl -X POST https://kauth.kakao.com/oauth/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code" \
  -d "client_id=f2a941eeb74d1c0cb0d136fbdacad0a1" \
  -d "redirect_uri=http://localhost:8080" \
  -d "code=위에서_복사한_인가코드"
```

### 응답 예시:

```json
{
  "token_type": "bearer",
  "access_token": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
  "expires_in": 21599,
  "refresh_token": "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy",
  "refresh_token_expires_in": 5183999
}
```

**`access_token` 값을 복사하세요!**

---

## 🎯 방법 3: Postman OAuth 2.0 사용

### Postman에서 설정

1. Postman에서 `Authorization` 탭 선택
2. Type: `OAuth 2.0` 선택
3. `Configure New Token` 클릭
4. 다음 정보 입력:

```
Token Name: Kakao Login
Grant Type: Authorization Code
Callback URL: http://localhost:8080
Auth URL: https://kauth.kakao.com/oauth/authorize
Access Token URL: https://kauth.kakao.com/oauth/token
Client ID: f2a941eeb74d1c0cb0d136fbdacad0a1
Client Secret: (비워두기)
Scope: (비워두기)
```

5. `Get New Access Token` 클릭
6. 카카오 로그인
7. 토큰 복사!

---

## 🚀 받은 토큰으로 API 테스트

### 1. 카카오 사용자 정보 조회 테스트

```bash
curl -X GET https://kapi.kakao.com/v2/user/me \
  -H "Authorization: Bearer {받은_액세스_토큰}"
```

**성공하면 다음과 같은 응답:**
```json
{
  "id": 1234567890,
  "kakao_account": {
    "email": "user@example.com",
    "profile": {
      "nickname": "홍길동",
      "profile_image_url": "https://..."
    }
  }
}
```

### 2. 백엔드 소셜 로그인 API 테스트

```bash
curl -X POST http://localhost:8080/api/auth/social/kakao \
  -H "Authorization: Bearer {받은_액세스_토큰}"
```

**신규 회원 응답:**
```json
{
  "code": 201,
  "message": "소셜 로그인 성공. 추가 정보를 입력해주세요.",
  "data": {
    "type": "Signup",
    "tempAccessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "provider": "KAKAO",
    "email": "user@example.com"
  }
}
```

---

## 🎯 빠른 테스트용 스크립트

토큰 받기가 귀찮다면 다음 스크립트를 사용하세요:

```bash
#!/bin/bash

# 카카오 설정
KAKAO_CLIENT_ID="f2a941eeb74d1c0cb0d136fbdacad0a1"
REDIRECT_URI="http://localhost:8080"

echo "================================"
echo "카카오 액세스 토큰 발급"
echo "================================"
echo ""
echo "1단계: 브라우저에서 다음 URL로 접속하세요:"
echo ""
echo "https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code"
echo ""
echo "2단계: 로그인 후 리다이렉트된 URL에서 'code=' 뒤의 값을 복사하세요"
echo ""
read -p "인가 코드 입력: " AUTH_CODE
echo ""
echo "3단계: 액세스 토큰 발급 중..."
echo ""

curl -X POST https://kauth.kakao.com/oauth/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code" \
  -d "client_id=${KAKAO_CLIENT_ID}" \
  -d "redirect_uri=${REDIRECT_URI}" \
  -d "code=${AUTH_CODE}" | jq .

echo ""
echo "위 응답에서 'access_token' 값을 복사하세요!"
```

스크립트 저장 후 실행:
```bash
chmod +x get-kakao-token.sh
./get-kakao-token.sh
```

---

## ⚠️ 주의사항

### 액세스 토큰 유효기간
- **카카오**: 2시간
- 만료되면 다시 발급 받아야 합니다
- 실제 서비스에서는 refresh token으로 자동 갱신

### 개발 vs 프로덕션
- **개발**: 위 방법으로 수동 발급
- **프로덕션**: 프론트엔드에서 카카오 SDK 사용

### 카카오 계정 권한
- 테스트용 계정으로 먼저 테스트
- 본인 계정으로 테스트 가능
- 동의 항목 설정 확인 (프로필, 이메일)

---

## 🎉 완료!

이제 받은 액세스 토큰으로 다음을 테스트할 수 있습니다:

1. **카카오 API 직접 호출**
   ```bash
   curl -X GET https://kapi.kakao.com/v2/user/me \
     -H "Authorization: Bearer {토큰}"
   ```

2. **백엔드 소셜 로그인 API**
   ```bash
   curl -X POST http://localhost:8080/api/auth/social/kakao \
     -H "Authorization: Bearer {토큰}"
   ```

3. **Postman 컬렉션에서 테스트**
   - Variables에 `kakao_access_token` 설정
   - "소셜 로그인 - 카카오" 요청 실행

**추천 방법**: 카카오 개발자 콘솔의 REST API 테스트 도구 (가장 빠름!)

