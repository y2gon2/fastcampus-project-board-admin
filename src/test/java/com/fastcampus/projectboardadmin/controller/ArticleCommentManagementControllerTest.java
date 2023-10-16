package com.fastcampus.projectboardadmin.controller;

import com.fastcampus.projectboardadmin.config.SecurityConfig;
import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.dto.ArticleCommentDto;
import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fastcampus.projectboardadmin.service.ArticleCommentManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View Controller - 댓글 관리")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleCommentManagementController.class)
class ArticleCommentManagementControllerTest {

    private final MockMvc mvc;

    @MockBean private ArticleCommentManagementService articleCommentManagementService;

    public ArticleCommentManagementControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] 댓글 관리 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingArticleCommentsManagementView_thenReturnsArticleCommentsManagementView() throws Exception {
        // Given
        given(articleCommentManagementService.getArticleComments()).willReturn(List.of());

        // When & Then
        mvc.perform(get("/management/article-comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("management/article-comments"))
                .andExpect(model().attribute("Comments", List.of()));

        then(articleCommentManagementService).should().getArticleComments();
    }

    @DisplayName("[view][GET] 댓글 1개 - 정상 호출")
    @Test
    void givenNothing_whenRequestingArticleComment_thenReturnsArticleComment() throws Exception {
        // Given
        Long commentId = 1L;
        ArticleCommentDto articleCommentDto = createArticleCommentDto("댓글");
        given(articleCommentManagementService.getArticleComment(commentId)).willReturn(articleCommentDto);

        // When & Then
        mvc.perform(get("/management/article-comments/" + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.content").value(articleCommentDto.content()))
                .andExpect(jsonPath("$.userAccount.nickname").value(articleCommentDto.userAccountDto().nickname()));

        then(articleCommentManagementService).should().getArticleComment(commentId);
    }

    @DisplayName("[view][POST] 댓글 삭제 - 정상 호출")
    @Test
    void givenNothing_whenRequestingDeletion_thenRedirectsToArticleCommentManagement() throws Exception {
        // Given
        Long commentId = 1L;
        willDoNothing().given(articleCommentManagementService).deleteArticleComment(commentId);

        // When & Then
        mvc.perform(
                post("/management/article-comments/" + commentId)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/management/article-comments"))
                .andExpect(redirectedUrl("management/article-comments"));

        then(articleCommentManagementService).should().deleteArticleComment(commentId);
    }

    // --------------------- Fixture for Test ---------------------------
    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
                1L,
                1L,
                createUserAccountDto(),
                null,
                content,
                LocalDateTime.now(),
                "Uno",
                LocalDateTime.now(),
                "Uno"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "unoTest",
                "uno-test@email.com",
                "uno-test",
                "test memo"
        );
    }

}