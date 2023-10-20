package com.fastcampus.projectboardadmin.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleType {
    USER("ROLE_USER", "사용자"), // ROLE : spring-security 에서 문자열로 권한 표현을 하는 규칙
    MANAGER("ROLE_MANAGER", "운영자"),
    DEVELOPER("ROLE_DEVELOPER", "개발자"),
    ADMIN("ROLE_ADMIN", "관리자")
    ;

    @Getter private final String roleName;
    @Getter private final String Description;

}

