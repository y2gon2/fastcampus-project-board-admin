package com.fastcampus.projectboardadmin.controller;

import com.fastcampus.projectboardadmin.config.SecurityConfig;
import com.fastcampus.projectboardadmin.config.TestSecurityConfig;
import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.dto.AdminAccountDto;
import com.fastcampus.projectboardadmin.service.AdminAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @Import(TestSecurityConfig.class) 적용시 문제점
// test 에서 Spring Security 인증 과정을 매번 수행하지 않도록
// 미리 계정을 생성하고 인증을 통과하는 작업을 완료해 놓은 TestSecurityConfig.class 를
// import 하여 사용하였다.
// 그런데 해당 test 의 대상이 곧 인증 대상이므로, AdminAccount가  두번 정의되는 문제가 발생한다.
// (Error: Duplicate mock definition)
// 따라서 실제 application class (SecurityConfig.class) 를 사용하고, 관련 인증 처리를 모두 해주어야 한다.
@DisplayName("View Controller - Admin Account")
@Import(SecurityConfig.class)
@WebMvcTest(AdminAccountController.class)
class AdminAccountControllerTest {

    private final MockMvc mvc;

    @MockBean private AdminAccountService adminAccountService;

    public AdminAccountControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    // 앞에서 언급한대로 TestSecurtiyConfig.class import 를 쓸 수 없기 때문에 (Duplicate mock definition 발생)
    // security 설정을 내부에서 진행
    @BeforeTestMethod
    public void securitySetup() {
        given(adminAccountService.searchUser(anyString()))
                .willReturn(Optional.of(createAdminAccountDto()));

        // 가입용?
        given(adminAccountService.saveUser(
                anyString(),
                anyString(),
                anySet(),
                anyString(),
                anyString(),
                anyString()
        )).willReturn(createAdminAccountDto());
    }

    @WithMockUser(username = "tester", roles = "USER")
    @DisplayName("[View][GET] 관리자 계정 페이지 - 정상 호출")
    @Test
    void givenAuthorizedUser_whenRequestingAdminMembersView_thenReturnsAdminMembersView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/admin/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("admin/members"));
    }

    @WithMockUser(username = "tester", roles = "USER")
    @DisplayName("[data][GET] 관리자 리스트 - 정상 호출")
    @Test
    void givenAuthorizedUser_whenRequestingAdmin_thenReturnsAdmin() throws Exception {
        // Given
        given(adminAccountService.users()).willReturn(List.of());

        // When & Then
        mvc.perform(get("/api/admin/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        then(adminAccountService).should().users();
    }

    @WithMockUser(username = "tester", roles = "MANAGER")
    @DisplayName("[data][DELETE] 관리자 정보 삭제 - 정상 호출")
    @Test
    void givenAuthorizedUser_whenDeletingAdminAccount_thenDeletesAdminAccount() throws Exception {
        // Given
        String username = "uno";
        willDoNothing().given(adminAccountService).deleteUser(username);

        // When & Then
        mvc.perform(
                delete("/api/admin/members/" + username)
                        .with(csrf())
        )
                .andExpect(status().isNoContent());
        then(adminAccountService).should().deleteUser(username);

    }

    // -------------------- Fixture for Test --------------------
    private AdminAccountDto createAdminAccountDto() {
        return AdminAccountDto.of(
                "unoTest",
                "pw",
                Set.of(RoleType.USER),
                "uno-test@email.com",
                "uno-test",
                "test memo"
        );
    }
}