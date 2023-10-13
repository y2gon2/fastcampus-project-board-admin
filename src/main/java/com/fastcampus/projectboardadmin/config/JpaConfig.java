package com.fastcampus.projectboardadmin.config;


import com.fastcampus.projectboardadmin.dto.security.BoardAdminPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// @EnableJpaAuditing
// Auditing 기능 활성화하여 AuditingField 상송 받은 Entity 들의 부모 class가 작동하도록 설정
@EnableJpaAuditing
@Configuration
public class JpaConfig {

    /**
     * Spring Security 의 인증 기능을 통과 시키고, 통과된 계정의 userName 을 반환 받는다.
     * */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional
                .ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(BoardAdminPrincipal.class::cast)
                .map(BoardAdminPrincipal::getUsername);
    }
}
