package com.fastcampus.projectboardadmin.service;

import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fastcampus.projectboardadmin.dto.properties.ProjectProperty;
import com.fastcampus.projectboardadmin.dto.response.UserAccountClientResponse;
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

import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@DisplayName("Business Logic - 일반 회원 관리")
@ActiveProfiles("test")
class UserAccountManagementServiceTest {

    // 1. 실제 API server 통신 상태 test
    @Disabled("실제 API 호출 결과 확인을 위한 test 이므로 logic test 상황에서는 비활성화")
    @DisplayName("실제 API 호출 Test")
    @SpringBootTest
    @Nested
    class RealApiTest {

        private final UserAccountManagementService sut;

        public RealApiTest(UserAccountManagementService sut) {
            this.sut = sut;
        }

        @DisplayName("일반 회원 정보 API 를 호출하면, 회원 정보를 가저온다.")
        @Test
        void givenNothing_whenCallUserAccountsApi_thenReturnsUserAccounts() {
            // Given

            // When
            List<UserAccountDto> result = sut.getUsers();

            // Then
            System.out.println(result.stream().findFirst());
            assertThat(result).isNotNull();
        }
    }

    // 2. Mocking server 를 사용하는 test
    @DisplayName("API Mocking test")
    @EnableConfigurationProperties(ProjectProperty.class)
    @AutoConfigureWebClient(registerRestTemplate = true)
    @RestClientTest(UserAccountManagementService.class)
    @Nested
    class RestTemplateTest {

        private  final UserAccountManagementService sut;
        private final ProjectProperty projectProperty;
        private final MockRestServiceServer server;
        private final ObjectMapper mapper;

        @Autowired
        public RestTemplateTest(
                UserAccountManagementService sut,
                ProjectProperty projectProperty,
                MockRestServiceServer server,
                ObjectMapper mapper
        ) {
            this.sut = sut;
            this.projectProperty = projectProperty;
            this.server = server;
            this.mapper = mapper;
        }

        // TODO: 게시글과 달리 화면 요청 api 로 확인했을 때 회원 정보를 한방에 모아서 보내주는 건 없었는데...
        //       CRUD 요청에서 findAll 로 하면 다 가져올수 있는건지... 모르겠다..
        @DisplayName("일반 회원 정보 목록 API 를 호출하면, 전체 일반 회원 정보를 가져온다.")
        @Test
        void givenNothing_whenCallingUserAccountsApi_thenReturnsUserAccounts() throws Exception {
            // Given
            UserAccountDto expectedUser = createUserAccountDto("uno", "Uno");
            UserAccountClientResponse expectedResponse = UserAccountClientResponse.of(List.of(expectedUser));

            server
                    .expect(requestTo(projectProperty.board().url() + "/management/user-accounts"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedResponse),
                            MediaType.APPLICATION_JSON
                    ));

            // When
            List<UserAccountDto> result = sut.getUsers();

            // Then
            assertThat(result).first()
                    .hasFieldOrPropertyWithValue("userId", expectedUser.userId())
                    .hasFieldOrPropertyWithValue("nickname", expectedUser.nickname());

            server.verify();
        }

        @DisplayName("회원 ID와 함께 회원 API를 호출하면, 회원을 가져온다.")
        @Test
        void givenUserAccountId_whenCallingUserAccountApi_thenReturnsUserAccount() throws Exception {
            // Given
            String userId = "uno";
            UserAccountDto expectedUser = createUserAccountDto(userId, "Uno");

            server
                    .expect(requestTo(projectProperty.board().url() + "/management/user-accounts/" + userId))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedUser),
                            MediaType.APPLICATION_JSON
                    ));

            // When
            UserAccountDto result = sut.getUser(userId);

            // Then
            assertThat(result)
                    .hasFieldOrPropertyWithValue("userId", expectedUser.userId())
                    .hasFieldOrPropertyWithValue("nickname", expectedUser.nickname());

            server.verify();
        }

        @DisplayName("회원 ID와 함께 삭제 API를 호출하면, 회원 정보를 삭제한다.")
        @Test
        void givenUserAccountId_whenCallingDeleteUserAccountApi_thenReturnsUserAccount() throws Exception {
            // Given
            String userId = "uno";
            server
                    .expect(requestTo(projectProperty.board().url() + "/management/user-accounts/" + userId))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());

            // When
            sut.deleteUser(userId);

            // Then
            server.verify();
        }
    }

    // -------------- Fixture for Test ----------------
    private UserAccountDto createUserAccountDto(String userId, String nickname) {
        return UserAccountDto.of(
                userId,
                "uno-test@email.com",
                nickname,
                "test memo"
        );
    }

}