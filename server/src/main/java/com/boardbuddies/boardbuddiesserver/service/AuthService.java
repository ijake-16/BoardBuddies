package com.boardbuddies.boardbuddiesserver.service;

import com.boardbuddies.boardbuddiesserver.domain.User;
import com.boardbuddies.boardbuddiesserver.dto.auth.*;
import com.boardbuddies.boardbuddiesserver.repository.UserRepository;
import com.boardbuddies.boardbuddiesserver.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SocialLoginService socialLoginService;
    private final JwtUtil jwtUtil;

    /**
     * 소셜 로그인 처리
     * - 기존 회원: 로그인 성공, 토큰 발급
     * - 신규 회원: 임시 토큰 발급 (DB 저장 X, 추가 정보 입력 필요)
     */
    @Transactional
    public Object processSocialLogin(SocialProvider provider, String socialAccessToken) {
        // 소셜 제공자로부터 사용자 정보 가져오기
        SocialLoginService.SocialUserInfo socialUserInfo = socialLoginService.getUserInfo(provider, socialAccessToken);

        // DB에서 사용자 조회
        User user = userRepository.findBySocialProviderAndSocialId(
                provider, socialUserInfo.getSocialId()).orElse(null);

        // 기존 회원 - 로그인 처리
        if (user != null && user.getIsRegistered()) {
            log.info("기존 회원 로그인: userId={}", user.getId());

            // JWT 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId());

            // 리프레시 토큰 업데이트
            user.updateRefreshToken(refreshToken);

            return SocialLoginResponse.builder()
                    .type(AuthType.Login)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }

        // 신규 회원 (또는 가입 미완료) - 임시 토큰 발급 (DB 저장 안함)
        log.info("신규 회원(또는 미완료) 소셜 인증 성공: provider={}, socialId={}", provider, socialUserInfo.getSocialId());

        String tempAccessToken = jwtUtil.generateTempAccessToken(
                socialUserInfo.getSocialId(),
                provider.name(),
                socialUserInfo.getEmail());

        return TempTokenResponse.builder()
                .type(AuthType.Signup)
                .tempAccessToken(tempAccessToken)
                .provider(provider)
                .email(socialUserInfo.getEmail())
                .build();
    }

    /**
     * 회원가입 완료 처리 (추가 정보 입력)
     * 
     * @param tempToken 임시 토큰
     * @param request   추가 정보
     * @return 회원가입 완료 응답 (정식 토큰 발급)
     */
    @Transactional
    public SocialLoginResponse completeSignup(String tempToken, SignupCompleteRequest request) {
        // 임시 토큰 검증 및 정보 추출
        if (!jwtUtil.validateToken(tempToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Claims claims = jwtUtil.getClaimsFromToken(tempToken);
        String type = claims.get("type", String.class);
        if (!"temp".equals(type)) {
            throw new RuntimeException("임시 토큰이 아닙니다.");
        }

        String socialId = claims.get("socialId", String.class);
        String providerStr = claims.get("provider", String.class);
        String email = claims.get("email", String.class);
        SocialProvider provider = SocialProvider.valueOf(providerStr);

        // 이미 가입된 사용자인지 재확인 (동시성 이슈 등 방지)
        if (userRepository.findBySocialProviderAndSocialId(provider, socialId).isPresent()) {
            throw new RuntimeException("이미 가입된 사용자입니다.");
        }

        // 학번 중복 체크
        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new RuntimeException("이미 사용 중인 학번입니다.");
        }

        // 전화번호 중복 체크
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("이미 사용 중인 전화번호입니다.");
        }

        // 사용자 생성 및 저장
        User user = User.builder()
                .socialProvider(provider)
                .socialId(socialId)
                .email(email)
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .school(request.getSchool())
                .studentId(request.getStudentId())
                .gender(request.getGender())
                .phoneNumber(request.getPhoneNumber())
                .isRegistered(true)
                .role(com.boardbuddies.boardbuddiesserver.domain.Role.GUEST) // 기본 권한 설정 (필요 시)
                .build();

        user = userRepository.save(user);

        log.info("회원가입 완료 및 저장: userId={}, name={}", user.getId(), user.getName());

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 리프레시 토큰 저장
        user.updateRefreshToken(refreshToken);

        return SocialLoginResponse.builder()
                .type(AuthType.Signup)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 토큰 재발급
     * 
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰과 리프레시 토큰
     */
    @Transactional
    public SocialLoginResponse refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 토큰 타입 확인
        String tokenType = jwtUtil.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("리프레시 토큰이 아닙니다.");
        }

        // 사용자 ID 추출
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // DB에 저장된 리프레시 토큰과 비교
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 새로운 토큰 생성
        String newAccessToken = jwtUtil.generateAccessToken(user.getId());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 새로운 리프레시 토큰 저장
        user.updateRefreshToken(newRefreshToken);

        log.info("토큰 재발급 완료: userId={}", user.getId());

        return SocialLoginResponse.builder()
                .type(AuthType.Login)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
