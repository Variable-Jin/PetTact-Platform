package com.pettact.api.security.vo;

import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.code.repository.CommonCodeRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final CommonCodeRepository commonCodeRepository;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = extractEmail(provider, attributes);
        String providerId = extractProviderId(provider, attributes);

        if (email == null || providerId == null) {
            throw new OAuth2AuthenticationException("소셜 로그인 정보 추출 실패");
        }

        // 권한
        CommonCode role = commonCodeRepository.findById("ROLE_NORMAL")
            .orElseThrow(() -> new IllegalStateException("기본 권한 코드 없음"));
        
        // 상태
        CommonCode status = commonCodeRepository.findById("STATUS_ACTIVE")
            .orElseThrow(() -> new IllegalStateException("기본 상태 코드 없음"));
        
        // 이메일 + providerId 기준으로 사용자 조회
        Users user = userRepository.findByUserEmail(email)
                .orElseGet(() -> {
                	Users newUser = Users.builder()
                		    .userEmail(email)
                		    .provider(provider)
                		    .providerId(providerId)
                		    .userName(extractName(provider, attributes))           // 이름
                		    .userNickname(generateUniqueNickname(email))           // 임시 닉네임 생성
                		    .userEmailChecked(true)
                		    .userHasPet(false)
                		    .userBlacklist(false)
                		    .userCreatedAt(LocalDateTime.now())
                		    .roleCode(role)                            // 기본 권한
                		    .statusCode(status)                        // 기본 상태
                		    .build();
                    return userRepository.save(newUser);
                });

        return new CustomOAuth2User(user, attributes);
    }

    private String extractEmail(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("email");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            return (String) account.get("email");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("email");
        }
        return null;
    }

    private String extractProviderId(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub"); // Google은 sub가 고유 ID
        } else if ("kakao".equals(provider)) {
            return String.valueOf(attributes.get("id")); // long → string 변환
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("id");
        }
        return null;
    }
    
    private String extractName(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        } else if ("kakao".equals(provider)) {
            Map<String, Object> props = (Map<String, Object>) attributes.get("properties");
            return (String) props.get("nickname");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return (String) response.get("name");
        }
        return "Unknown";
    }

    private String generateUniqueNickname(String email) {
        String base = email.split("@")[0];
        String nickname = base;
        int i = 1;
        while (userRepository.existsByUserNickname(nickname)) {
            nickname = base + i++;
        }
        return nickname;
    }
}

