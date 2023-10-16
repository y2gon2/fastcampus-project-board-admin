package com.fastcampus.projectboardadmin.domain;

import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.domain.converter.RoleTypesConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "email", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class AdminAccount extends AuditingFields {
    @Id @Column(length = 50) private String userId;

    @Setter @Column(nullable = false) private String userPassword;

    // [1,2,3] -> table_column : 문자열로 통으로 저장 후 꺼낼때 collection 을 치환
    // -> attribute converter 를 만들어서 사용.
    @Convert(converter = RoleTypesConverter.class)
    @Column(nullable = false)
    private Set<RoleType> roleTypes = new LinkedHashSet<>();

    @Setter @Column(length = 100) private String email;
    @Setter @Column(length = 100) private String nickname;
    @Setter private String memo;

    protected AdminAccount() {}

    private AdminAccount(
            String userId,
            String userPassword,
            Set<RoleType> roleTypes,
            String email,
            String nickname,
            String memo,
            String createdBy // OAuth 인증 받지 않은 상태에서 사용자가 사용할 수 있도록??
    ) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.roleTypes = roleTypes;
        this.email = email;
        this.nickname = nickname;
        this.memo = memo;
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
    }

    // 인증 정보가 필요 없는 경우
    public static AdminAccount of(
            String userId,
            String userPassword,
            Set<RoleType> roleTypes,
            String email,
            String nickname,
            String memo
    ) {
        return AdminAccount.of(
                userId,
                userPassword,
                roleTypes,
                email,
                nickname,
                memo,
                null
        );
    }

    // 인증 정보가 필요한 경우
    public static AdminAccount of(
            String userId,
            String userPassword,
            Set<RoleType> roleTypes,
            String email,
            String nickname,
            String memo,
            String createdBy
    ) {
        return new AdminAccount(
                userId,
                userPassword,
                roleTypes,
                email,
                nickname,
                memo,
                createdBy
        );
    }

    public void addRoleType(RoleType roleType) {
        this.getRoleTypes().add(roleType);
    }

    public void addRoleTypes(Collection<RoleType> roleTypes) {
        this.getRoleTypes().addAll(roleTypes);
    }

    public void removeRoleType(RoleType roleType) {
        this.getRoleTypes().remove(roleType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminAccount that = (AdminAccount) o;
        return Objects.equals(this.getUserId(), that.getUserId());  // 직접 field 조회에서 getter 사용으로 변경. proxy 객체 직접 접근시 발생하는 문제 해결 ?
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());  // 직접 field 조회에서 getter 사용으로 변경. proxy 객체 직접 접근시 발생하는 문제 해결 ?
    }
}
