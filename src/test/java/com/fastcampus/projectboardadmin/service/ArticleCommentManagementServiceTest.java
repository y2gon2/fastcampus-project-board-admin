package com.fastcampus.projectboardadmin.service;

import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.dto.ArticleCommentDto;
import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fastcampus.projectboardadmin.dto.properties.ProjectProperty;
import com.fastcampus.projectboardadmin.dto.response.CommentClientResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@ActiveProfiles("test")
@DisplayName("Business Logic - 댓글 관리")
class ArticleCommentManagementServiceTest {

    // 1. 실제 API server 통신 상태 test
//    @Disabled("실재 API 호출 결과 관찰이 필요할 경우 활성화")
    @DisplayName("실제 API 호출 TEST")
    @SpringBootTest
    @Nested
    class RealApiTest {
        private final ArticleCommentManagementService sut;

        @Autowired
        public RealApiTest(ArticleCommentManagementService sut) {
            this.sut = sut;
        }

        @DisplayName("댓글 API 를 호출하면, 댓글을 가져온다")
        @Test
        void givenNothing_whenCallingCommentsApi_thenReturnsComments() {
            // Given

            // When
            List<ArticleCommentDto> result = sut.getArticleComments();

            // Then
            System.out.println(result.stream().findFirst());
            assertThat(result).isNotNull();
        }
    }

    // 2. Mocking server 를 사용하는 test
    @DisplayName("API mocking test")
    @EnableConfigurationProperties(ProjectProperty.class)
    @AutoConfigureWebClient(registerRestTemplate = true)
    @RestClientTest(ArticleCommentManagementService.class)
    @Nested
    class RestTemplateTest {

        private final ArticleCommentManagementService sut;
        private final ProjectProperty projectProperty;
        private final MockRestServiceServer server;
        private final ObjectMapper mapper;

        @Autowired
        public RestTemplateTest(
                ArticleCommentManagementService sut,
                ProjectProperty projectProperty,
                MockRestServiceServer server,
                ObjectMapper mapper) {
            this.sut = sut;
            this.projectProperty = projectProperty;
            this.server = server;
            this.mapper = mapper;
        }

        // TODO: 게시글과 달리 화면 요청 api 로 확인했을 때 회원 정보를 한방에 모아서 보내주는 건 없었는데...
        //       CRUD 요청에서 findAll 로 하면 다 가져올수 있는건지... 모르겠다..
        @DisplayName("댓글 목록 API를 호출하면, 댓글들을 가져온다")
        @Test
        void givenNothing_whenCallingArticleCommentsApi_thenReturnsArticleComments() throws Exception {
            // given
            ArticleCommentDto expectedComment = createArticleCommentDto("댓글");
            CommentClientResponse expectedResponse = CommentClientResponse.of(List.of(expectedComment));

            server
                    .expect(requestTo(projectProperty.board().url() + "/api/articleComments?size=10000"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedResponse),
                            MediaType.APPLICATION_JSON
                    ));

            // when
            List<ArticleCommentDto> result = sut.getArticleComments();

            // then
            assertThat(result).first()
                    .hasFieldOrPropertyWithValue("id", expectedComment.id())
                    .hasFieldOrPropertyWithValue("content", expectedComment.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedComment.userAccount().nickname());

            server.verify();
        }

        @DisplayName("댓글 API를 호출하면, 댓글을 가져온다")
        @Test
        void givenNothing_whenCallingArticleCommentApi_thenReturnsArticleComment() throws Exception {
            // given
            Long commentId = 1L;
            ArticleCommentDto expectedComment = createArticleCommentDto("댓글");

            server
                    .expect(requestTo(projectProperty.board().url() + "/api/articleComments/" + commentId + "?projection=withUserAccount"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedComment),
                            MediaType.APPLICATION_JSON
                    ));

            // when
            ArticleCommentDto result = sut.getArticleComment(commentId);

            // then
            assertThat(result)
                    .hasFieldOrPropertyWithValue("id", expectedComment.id())
                    .hasFieldOrPropertyWithValue("content", expectedComment.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedComment.userAccount().nickname());
            server.verify();
        }

        @DisplayName("댓글 ID 로 댓글 삭제 API를 호출하면, 댓글을 삭제한다.")
        @Test
        void givenNothing_whenCallingDeleteArticleCommentApi_thenReturnsDeleteArticleComment() throws Exception {
            // given
            long commentId = 1L;

            server
                    .expect(requestTo(projectProperty.board().url() + "/api/articleComments/" + commentId))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());

            // when
            sut.deleteArticleComment(commentId);

            // then
            server.verify();
        }
    }

    // ------------- Fixture for Test ----------------

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