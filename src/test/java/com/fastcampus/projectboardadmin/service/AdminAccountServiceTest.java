package com.fastcampus.projectboardadmin.service;

import com.fastcampus.projectboardadmin.domain.AdminAccount;
import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.dto.AdminAccountDto;
import com.fastcampus.projectboardadmin.repository.AdminAccountRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL_LONG;
import static org.mockito.BDDMockito.*;


@DisplayName("Business Logic - 관리자 회원")
@ExtendWith(MockitoExtension.class) // Slice test X Mock test 만 사용
class AdminAccountServiceTest {

    @InjectMocks private AdminAccountService sut;
    @Mock private AdminAccountRepository adminAccountRepository;

    @DisplayName("존재하는 관리자 ID 를 검색하면, Optional 로 관리자 정보를 반환")
    @Test
    void givenExistentAdminId_whenSearching_thenReturnsOptionalAdminAccountData() {
        // Given
        String username = "uno";
        given(adminAccountRepository.findById(username)).willReturn(Optional.of(createAdminAccount(username, Set.of(RoleType.USER))));

        // When
        Optional<AdminAccountDto> result = sut.searchUser(username);

        // Then
        assertThat(result).isPresent();
        then(adminAccountRepository).should().findById(username);
    }

    @DisplayName("존재하지 않는 관리자 ID 를 검색하면, 빈 Optional 을 반환")
    @Test
    void givenNonexistentAminId_whenSearching_thenReturnsEmptyOptional() {
        // Given
        String username = "no_uno";
        given(adminAccountRepository.findById(username)).willReturn(Optional.empty());

        // When
        Optional<AdminAccountDto> result = sut.searchUser(username);

        // Then
        assertThat(result).isEmpty();
        then(adminAccountRepository).should().findById(username);
    }

    @DisplayName("관리자 정보를 입력하면, 새로운 관리자를 저장하여 가입시키고 해당 관리자 정보를 반환")
    @Test
    void givenAdminParams_whenSaving_thenSavesAdminAccount() {
        // Given
        AdminAccount admin = createAdminAccount("uno", Set.of(RoleType.USER));
        given(adminAccountRepository.save(admin)).willReturn(admin);

        // When
        AdminAccountDto result = sut.saveUser(
                admin.getUserId(),
                admin.getUserPassword(),
                admin.getRoleTypes(),
                admin.getEmail(),
                admin.getNickname(),
                admin.getMemo()
        );

        // Then
        assertThat(result)
                .hasFieldOrPropertyWithValue("userId", admin.getUserId())
                .hasFieldOrPropertyWithValue("userPassword", admin.getUserPassword())
                .hasFieldOrPropertyWithValue("roleType", admin.getRoleTypes())
                .hasFieldOrPropertyWithValue("email", admin.getEmail())
                .hasFieldOrPropertyWithValue("nickname", admin.getNickname())
                .hasFieldOrPropertyWithValue("memo", admin.getMemo())
                .hasFieldOrPropertyWithValue("createdBy", admin.getCreatedAt())
                .hasFieldOrPropertyWithValue("modifiedBy", admin.getModifiedBy());

        then(adminAccountRepository).should().save(admin);
    }


    @DisplayName("전체 관리자 회원 조회")
    @Test
    void givenNothing_whenRequestingAdminAccounts_thenReturnsAdminAccounts () {
        // Given
        given(adminAccountRepository.findAll()).willReturn(List.of());

        // When
        List<AdminAccountDto> result = sut.users();

        // Then
        assertThat(result).hasSize(0);
        then(adminAccountRepository).should().findAll();
    }

    @DisplayName("관리자 ID 를 입력하면, 관리자을 삭제한다.")
    @Test
    void givenAdminAccountId_whenDeleting_thenDeletesAdminAccount() {
        // Given
        String userId = "uno";
        willDoNothing().given(adminAccountRepository).deleteById(userId);

        // When
        sut.deleteUser(userId);

        // Then
        then(adminAccountRepository).should().deleteById(userId);
    }

    // -------------------- Fixture for Test ---------------------

    private AdminAccount createAdminAccount(String username, Set<RoleType> roleType) {
        return AdminAccount.of(
                username,
                "password",
                Set.of(RoleType.ADMIN),
                "a@email.com",
                "Uno",
                "memo"
        );
    }

}