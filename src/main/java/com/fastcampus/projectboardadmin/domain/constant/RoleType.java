package com.fastcampus.projectboardadmin.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleType {
    USER("ROLE_USER"), // ROLE : spring-security 에서 문자열로 권한 표현을 하는 규칙
    MANAGER("ROLE_MANAGER"),
    DEVELOPER("ROLE_DEVELOPER"),
    ADMIN("ROLE_ADMIN")
    ;

    @Getter private final String roleName;

}

