package com.fastcampus.projectboardadmin.service;

import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.dto.ArticleDto;
import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fastcampus.projectboardadmin.dto.properties.ProjectProperty;
import com.fastcampus.projectboardadmin.dto.response.ArticleClientResponse;
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
@DisplayName("Business Logic - 게시글 관리")
class ArticleManagementServiceTest {

    // 기존 db 에 연결해서 data CRUD 작업을 진행했었으나,
    // API 통신을 통해 작업을 진행 하는 것으로 바꿈
    // 해당 과정에서 REST template 중간에 필요해 진다.
    //
    // 왜 해당 test 에서 이런 내용의 작업이 필요할까?
    // -> data source 가 외부(게시글 프로젝트 내 DB)에 존재하기 때문

    // 1. 실제 API 로부터 받은 data 를 사용하는 test
    @Disabled("실제 API 호출 결과 관찰용이므로 평상시에는 비활성화")
    @DisplayName("실제 API 호출 TEST")
    @SpringBootTest
    @Nested
    class RealApiTest {
        private final ArticleManagementService sut;

        @Autowired
        public RealApiTest(ArticleManagementService sut) {
            this.sut = sut;
        }

        @DisplayName("게시글 API 를 호출하면, 게시글들을 가져온다.")
        @Test
        void givenNothing_whenCallingArticlesApi_thenReturnsArticles() {
            // Given

            // When
            ArticleDto result = sut.getArticle(1L);

            // Then
            // 요청 사항이 예상한바와 일치하는가가 중요하기보다 실제 API 호출에 대한 응답을 받았는지가 중요
            System.out.println(result);
            assertThat(result).isNotNull();
        }
    }

    // 2. Mocking server 를 사용하는 test
    @DisplayName("API mocking TEST")
    @EnableConfigurationProperties(ProjectProperty.class)  // slice test 할때 configurationProperty 는 기본 비활성화 이므로 이를 활성화 시킴.
    @AutoConfigureWebClient(registerRestTemplate = true) // 사용자가 직접 가져올 bean 을 설정?
    @RestClientTest(ArticleManagementService.class)
    @Nested
    class RestTemplateTest {

        private final ArticleManagementService sut;
        private final ProjectProperty projectProperties; // 게시판 서비스 url 값이 application.yaml 에 저장되어 있어 해당 값을 가져옴.
        private final MockRestServiceServer server; // Mocking data를 사용하는 test
        private final ObjectMapper mapper;

        @Autowired
        public RestTemplateTest(
                ArticleManagementService sut,
                ProjectProperty projectProperties,
                MockRestServiceServer server,
                ObjectMapper mapper
        ) {
            this.sut = sut;
            this.projectProperties = projectProperties;
            this.server = server;
            this.mapper = mapper;
        }

        @DisplayName("게시글 목록 API를 호출하면, 게시글들을 가져온다.")
        @Test
        void givenNothing_whenCallingArticlesApi_thenReturnsArticleList() throws Exception {
            // Given
            ArticleDto expectedArticle = createArticleDto("제목", "내용");
            ArticleClientResponse expectedResponse = ArticleClientResponse.of(List.of(expectedArticle));

            // 해당 project 에서는 게시글, 댓글 등 게시판 project web 으로 부터
            // 전체 data 를 받아서 Spring 에서 지원하는 paging 기능을 사용할 예정이다.
            // 그러나 실제 프로젝트에서 엄청나게 큰 data 전체를 받는 것은 매우 비효율 적인 경우가 많으므로,
            // response 된 data 는 paging 된 data 인 것이 기본이다.
            // (그래서 page key - value 가존 존재한다.)
            // 그런데 현 project 에서는 앞에서 언급된 바와 같이 모든 data 를 받아 처리할 것이기 때문애
            // 어떻게 paging 된 전송 data 를 받아서 어떻게 전체 data 로 다룰 것인지에 대한 작업이 필요해 진다.
            // 여기에서는 요청 size 를 임의의 큰 수로 하여 DB data 수가 해당 size 보다 작다면, 전체를 가져올 수 있도록
            // 해당 작업을 단순화한 전략을 사용함.
            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articles?size=10000"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedResponse),
                            MediaType.APPLICATION_JSON
                    ));

            // When
            List<ArticleDto> result = sut.getArticles();

            // Then
            assertThat(result).first()
                    .hasFieldOrPropertyWithValue("id", expectedArticle.id())
                    .hasFieldOrPropertyWithValue("title", expectedArticle.title())
                    .hasFieldOrPropertyWithValue("content", expectedArticle.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticle.userAccount().nickname());
            server.verify();
        }

        @DisplayName("게시글 API를 호출하면, 게시글을 가져온다.")
        @Test
        void givenNothing_whenCallingArticleApi_thenReturnArticle() throws Exception {
            // Given
            Long articleId = 1L;
            ArticleDto expectedArticle = createArticleDto("제목", "내용");

            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articles/" + articleId + "?projection=withUserAccount"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedArticle),
                            MediaType.APPLICATION_JSON
                    ));

            // When
            ArticleDto result = sut.getArticle(articleId);

            // Then
            assertThat(result)
                    .hasFieldOrPropertyWithValue("id", expectedArticle.id())
                    .hasFieldOrPropertyWithValue("title", expectedArticle.title())
                    .hasFieldOrPropertyWithValue("content", expectedArticle.content())
                    .hasFieldOrPropertyWithValue("userAccount.nickname", expectedArticle.userAccount().nickname());
            server.verify();
        }

        @DisplayName("게시글 ID 와 함께 게시글 삭제 API를 호출하면, 게시글을 삭제한다.")
        @Test
        void givenNothing_whenCallingDeleteArticle_thenDeleteArticle() throws Exception {
            // Given
            Long articleId = 1L;

            server
                    .expect(requestTo(projectProperties.board().url() + "/api/articles/" + articleId))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());

            // When
            sut.deleteArticle(articleId);

            // Then
            server.verify();
        }
    }






    private ArticleDto createArticleDto(String title, String content) {
        return ArticleDto.of(
                1L,
                createUserAccountDto(),
                title,
                content,
                null,
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