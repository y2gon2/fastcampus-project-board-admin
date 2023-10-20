package com.fastcampus.projectboardadmin.config;

import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.dto.security.BoardAdminPrincipal;
import com.fastcampus.projectboardadmin.dto.security.KakaoOAuth2Response;
import com.fastcampus.projectboardadmin.service.AdminAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

// Spring Security 인증 과정 (by ChatGPT)
// 질문
// application 실행 중  .formLogin(Customizer.withDefaults()) method 실행 요청 입력된다면,
// UserDetailsService 객체 정보를 가지고 인증 작업을 진행하는건가?
//
// ChatGPT
// 네, 맞습니다. .formLogin(Customizer.withDefaults())는 폼 기반 인증을 활성화합니다.
// 그리고 기본적으로, 폼 기반 인증은 사용자가 제공한 사용자 이름(username)과 비밀번호(password)를 이용하여 인증을 진행합니다.
//
// Spring Security의 기본 구성에 따르면, UserDetailsService는 사용자 이름으로 UserDetails 객체를 로드하는 역할을 합니다.
// UserDetails 객체는 사용자의 사용자 이름, 비밀번호, 권한, 계정 상태 정보(계정 만료, 잠김 상태 등)를 포함합니다.
//
// 인증 과정은 다음과 같이 진행됩니다:
//
// 사용자가 로그인 폼에서 사용자 이름과 비밀번호를 입력하고 제출합니다.
// Spring Security는 UserDetailsService를 사용하여 입력된 사용자 이름과 일치하는 UserDetails 객체를 검색합니다.
// UserDetails 객체에서 가져온 비밀번호와 사용자가 폼에서 입력한 비밀번호를 비교합니다.
// 비밀번호가 일치하면 인증은 성공적으로 완료됩니다. 그렇지 않으면 인증 실패 예외가 발생합니다.
// 추가로, UserDetails 객체는 계정의 상태 정보 (예: 계정이 잠겼는지, 비밀번호가 만료되었는지 등)도 제공하므로,
// 해당 상태 정보를 기반으로 추가적인 검사가 수행될 수 있습니다.
// 따라서 UserDetailsService는 폼 기반 인증의 핵심 구성 요소 중 하나입니다.

@Configuration
public class SecurityConfig {

    // secruity 에 태워서 security 에 관리하에 두고 인증과 권한 체크 진행
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String [] rolesAboveManager = {
                RoleType.ADMIN.name(),
                RoleType.DEVELOPER.name(),
                RoleType.MANAGER.name()
        };

        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(HttpMethod.POST, "/**").hasAnyRole(rolesAboveManager) // mvcMatchers() 는 spring 3.0 에서 requestMatchers 로 변경 됨.
                        .mvcMatchers(HttpMethod.DELETE, "/**").hasAnyRole(rolesAboveManager)
                        .anyRequest().authenticated() // permitAll() 에서 인증 필요로 변경
                )
                .formLogin(withDefaults()) // 기본값을 내부적으로 설정. 따라서 method chaining 을 위한 and() 가 필요 없어짐.
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .oauth2Login(withDefaults())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(AdminAccountService adminAccountService) {
        return username -> adminAccountService
                .searchUser(username)
                .map(BoardAdminPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("해당 계정을 찾을수 없습니다. - username : " + username));
    }

    /**
     * <p>
     * OAuth 2.0 을 이용한 인증 정보를 처리
     * 카카오 인증 방식 선택
     *
     * @param adminAccountService 게시판 서비스의 사용자 계정을 다루는 service logic
     * @param passwordEncoder 패스워드 암호와 도구
     * @return {@link OAuth2UserService} OAuth2 인증 사용자 정보를 읽어들이고 처리하는 service instance 반환
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            AdminAccountService adminAccountService,
            PasswordEncoder passwordEncoder
    ) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());

            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String providerId = String.valueOf(kakaoResponse.id());
            String username = registrationId + "_" + providerId;
            String dummyPassword = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());
            Set<RoleType> roleTypes = Set.of(RoleType.USER);

            return adminAccountService.searchUser(username)
                    .map(BoardAdminPrincipal::from)
                    .orElseGet(() ->
                            BoardAdminPrincipal.from(
                                    adminAccountService.saveUser(
                                            username,
                                            dummyPassword,
                                            roleTypes,
                                            kakaoResponse.email(),
                                            kakaoResponse.nickname(),
                                            null
                                    )
                            )
                    );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

