package com.fastcampus.projectboardadmin.dto.security;

import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.dto.AdminAccountDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record BoardAdminPrincipal(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String email,
        String nickname,
        String memo,
        Map<String, Object> oAuth2Attributes
) implements UserDetails, OAuth2User {

    // oAuth2Attributes parameter X
    public static BoardAdminPrincipal of(
            String username,
            String password,
            Set<RoleType> roleTypes,
            String email,
            String nickname,
            String memo
    ) {
        return BoardAdminPrincipal.of(
                username,
                password,
                roleTypes,
                email,
                nickname,
                memo,
                Map.of()
        );
    }

    // oAuth2Attributes parameter O
    public static BoardAdminPrincipal of(
            String username,
            String password,
            Set<RoleType> roleTypes,
            String email,
            String nickname,
            String memo,
            Map<String, Object> oAuth2Attributes
    ) {
        return new BoardAdminPrincipal(
                username,
                password,
                roleTypes.stream()
                        .map(RoleType::getRoleName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet()),
                email,
                nickname,
                memo,
                oAuth2Attributes
        );
    }

    // UserAccountDto -> BoardPrincipal 을 만들어야 할 경우
    public static BoardAdminPrincipal from(AdminAccountDto dto) {
        return BoardAdminPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.roleTeyps(),
                dto.email(),
                dto.nickname(),
                dto.memo()
        );
    }

    // BoardPrincipal -> Dto
    public AdminAccountDto toDto() {
        return AdminAccountDto.of(
                username,
                password,
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(RoleType::valueOf)
                        .collect(Collectors.toUnmodifiableSet()),
                email,
                nickname,
                memo
        );
    }

    // for Spring security
    @Override public String getPassword() {
        return password;
    }
    @Override public String getUsername() {
        return username;
    }

    // 사용자 권한 설정 (ex. 관리자/일반사용자)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // for OAuth

    // provider 가 (내부 구조, key 명칭은 다르리 지라도)JSON key-value 로 인증 정보를 보내주면
    // 해당 정보 전체를 해당 mapping 구조로 받기 위한 method
    // 이렇게 받아서 field 값으로 저장, 작업을 진행한다.
    @Override public Map<String, Object> getAttributes() { return null; }
    @Override public String getName() { return username; }


}

