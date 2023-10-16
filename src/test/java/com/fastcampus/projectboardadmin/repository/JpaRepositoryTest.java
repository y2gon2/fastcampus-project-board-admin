package com.fastcampus.projectboardadmin.repository;


import com.fastcampus.projectboardadmin.domain.AdminAccount;
import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


// 현재 구현하고 있는 repository  들은 JpaRepository 를 상속함으로써,
// JPA 가 제공하는 모든 method 를 별도 구현없이 사용하고 있다.
// 이 말은 곧 대부분의 CRUD 관련 작업에 대해 작성자가 구현한 것이 아닌
// 제공되는 method 들을 수정하지 않고 사용한다는 말이다.
// 따라서, 이러한 method 들을 각각 별도의 test 진행는 것은 크게 의미가 없으므로,
// Jpa test 를 통해 제공되는 CRUD 동작이 제대로 동작하는지 general 상태로 한번만 test 를 진행한다.
@DisplayName("JPA 연결 Test")
@Import(JpaRepositoryTest.TestJpaConfig.class) // 아래 test 조건 때문에 test 용 Jpa configuration import
@DataJpaTest // slice test 로 모든 Spring Context 내 필요한 Bean Configuration 만 읽어옴. 때문에 test 용 Jpa configuration 을 별도로 생성하여 이를 사용해야 함.
class JpaRepositoryTest {

    private final AdminAccountRepository adminAccountRepository;


    public JpaRepositoryTest(@Autowired AdminAccountRepository adminAccountRepository) {
        this.adminAccountRepository = adminAccountRepository;
    }

    @DisplayName("회원정보 Select Test")
    @Test
    void givenUserAccounts_whenSelecting_thenWorkFine() {
        // Given


        // When
        Optional<AdminAccount> userAccouts = adminAccountRepository.findById("uno");

        // Then
        assertThat(userAccouts)
                .isNotNull();
    }

    @DisplayName("회원 정보 Insert Test")
    @Test
    void givenUserAccount_whenInserting_thenWorkFine() {
        // Given
        long previousCount = adminAccountRepository.count();
        AdminAccount adminAccount = AdminAccount.of(
                "test_id",
                "pw",
                Set.of(RoleType.DEVELOPER),
                "e@mail.com",
                "nickname",
                "memo"
        );

        // When
        adminAccountRepository.save(adminAccount);

        // Then
        assertThat(adminAccountRepository.count())
                .isEqualTo(previousCount + 1);
    }

    @DisplayName("회원 정보 Update Test")
    @Test
    void givenUserAccountAndRoleType_whenInserting_thenWorksFine() {
        // Given
        AdminAccount adminAccount = adminAccountRepository.getReferenceById("uno");
        adminAccount.addRoleType(RoleType.DEVELOPER);
        adminAccount.addRoleTypes(List.of(RoleType.USER, RoleType.USER));
        adminAccount.removeRoleType(RoleType.ADMIN);

        // When
        // 여기에서 save 요청은 기존에 존재하는 data 의 update 가 발생되어야 한다.
        // 그러나 JPA test 에서 기본적으로 영속성 작업에 대해 roll back mode 이다.
        // (원래 data 에 변경은 일반적으로 test 단계에서 원하는 작업이 아니므로)
        // 그래서 이런 작업을 test 는 통과 하지만, 최종 flush 단계에서 실제 data update query 는 발생하지 않는다.
        // UserAccount updatedAccount = userAccountRepository.save(userAccount);
        // 이렇게 강제적으로 바로 flush 를 하는 method 를 사용해야  update query 가 발생함을 확인할 수 있다. 
        // (test 끝난 후 내부적으로 별도의 rollback transaction 을 실행 시킴)
        AdminAccount updatedAccount = adminAccountRepository.saveAndFlush(adminAccount);

        // Then
        assertThat(updatedAccount)
                .hasFieldOrPropertyWithValue("userId", "uno")
                .hasFieldOrPropertyWithValue("roleTypes", Set.of(RoleType.DEVELOPER, RoleType.USER));
    }

    @DisplayName("회원정보 Delete Test")
    @Test
    void givenUserAccount_whenDeleting_thenWorkFine() {
        // Given
        long previousUserCount = adminAccountRepository.count();
        AdminAccount adminAccount = adminAccountRepository.getReferenceById("uno");

        // When
        adminAccountRepository.delete(adminAccount);

        // Then
        assertThat(adminAccountRepository.count())
                .isEqualTo(previousUserCount - 1);
    }

//-----------------------------------------
    @EnableJpaAuditing
    @TestConfiguration
    static class TestJpaConfig {
        @Bean
        AuditorAware<String> auditorAware() {
            return () -> Optional.of("uno");
        }
    }
}

