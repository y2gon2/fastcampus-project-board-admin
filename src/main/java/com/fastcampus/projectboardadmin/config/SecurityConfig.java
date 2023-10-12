package com.fastcampus.projectboardadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // secruity 에 태워서 security 에 관리하에 두고 인증과 권한 체크 진행
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(Customizer.withDefaults()) // 기본값을 내부적으로 설정. 따라서 method chaining 을 위한 and() 가 필요 없어짐.
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .build();
    }
}

