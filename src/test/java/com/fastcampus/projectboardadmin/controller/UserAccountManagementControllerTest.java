package com.fastcampus.projectboardadmin.controller;

import com.fastcampus.projectboardadmin.config.TestSecurityConfig;
import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fastcampus.projectboardadmin.service.UserAccountManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("View Controller - 회원 정보 관리")
@Import(TestSecurityConfig.class)
@WebMvcTest(UserAccountManagementController.class)
class UserAccountManagementControllerTest {

    private final MockMvc mvc;

    @MockBean private UserAccountManagementService userAccountManagementService;

    public UserAccountManagementControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @WithMockUser(username = "tester", roles = "USER")
    @DisplayName("[View][GET] 회원 관리 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingUserAccountManagementView_thenReturnsUserAccountManagementView() throws Exception {
        // Given
        given(userAccountManagementService.getUsers()).willReturn(List.of());

        // When & Then
        mvc.perform(get("/management/user-accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("management/user-accounts"))
                .andExpect(model().attribute("userAccounts", List.of()));

        then(userAccountManagementService).should().getUsers();
    }

    @WithMockUser(username = "tester", roles = "USER")
    @DisplayName("[View][GET] 1명의 회원 정보 - 정상 호출")
    @Test
    void givenUserId_whenRequestingUserAccount_thenReturnsUserAccount() throws Exception {
        // Given
        String userId = "uno";
        UserAccountDto userAccountDto = createAccountDto(userId, "Uno");
        given(userAccountManagementService.getUser(userId)).willReturn(userAccountDto);

        // When & Then
        mvc.perform(get("/management/user-accounts/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId))  // 그런데 json 에 userId 는 없는데???
                .andExpect(jsonPath("$.nickname").value(userAccountDto.nickname()));
        then(userAccountManagementService).should().getUser(userId);
    }

    @WithMockUser(username = "tester", roles = "MANAGER")
    @DisplayName("[View][POST] 회원 정보 삭제 - 정상 호출")
    @Test
    void givenUserId_whenRequestingDeletion_thenRedirectsToUserAccountManagement() throws Exception {
        // Given
        String userId = "uno";
        willDoNothing().given(userAccountManagementService).deleteUser(userId);

        // When & Then
        mvc.perform(
                    post("/management/user-accounts/" + userId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/management/user-accounts"))
                .andExpect(redirectedUrl("/management/user-accounts"));

        then(userAccountManagementService).should().deleteUser(userId);
    }

    // ---------------- Fixture for Test ------------------------
    private UserAccountDto createAccountDto(String userId, String nickname) {
        return UserAccountDto.of(
                userId,
                "uno-test@email.com",
                nickname,
                "test memo"
        );
    }
}