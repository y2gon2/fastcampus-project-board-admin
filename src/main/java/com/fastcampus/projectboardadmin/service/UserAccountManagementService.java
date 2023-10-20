package com.fastcampus.projectboardadmin.service;

import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fastcampus.projectboardadmin.dto.properties.ProjectProperty;
import com.fastcampus.projectboardadmin.dto.response.UserAccountClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserAccountManagementService {

    private final ProjectProperty projectProperty;
    private final RestTemplate restTemplate;

    public List<UserAccountDto> getUsers() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(projectProperty.board().url() + "/api/userAccounts")
                .queryParam("size", 10000)
                .build()
                .toUri();

        UserAccountClientResponse response = restTemplate.getForObject(uri, UserAccountClientResponse.class);

        return Optional.ofNullable(response)
                .orElseGet(UserAccountClientResponse::empty)
                .userAccounts();
    }

    public UserAccountDto getUser(String userId) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(projectProperty.board().url() + "/api/userAccounts/" + userId)
                .build()
                .toUri();

        UserAccountDto response = restTemplate.getForObject(uri, UserAccountDto.class);

        return Optional.ofNullable(response)
                .orElseThrow(
                        () -> new NoSuchElementException("사용자 계정이 없습니다. - userAccountId: " + userId)
                );
    }

    public void deleteUser(String userId) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(projectProperty.board().url() + "/api/userAccounts/" + userId)
                .build()
                .toUri();

        restTemplate.delete(uri);
    }
}


// projection 적용 응답 data
//{
//        "id" : 1,
//        "content" : "Vestibulum quam sapien, varius ut, blandit non, interdum in, ante. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis faucibus accumsan odio. Curabitur convallis.\nDuis consequat dui nec nisi volutpat eleifend. Donec ut dolor. Morbi vel lectus in quam fringilla rhoncus.\nMauris enim leo, rhoncus sed, vestibulum sit amet, cursus id, turpis. Integer aliquet, massa id lobortis convallis, tortor risus dapibus augue, vel accumsan tellus nisi eu orci. Mauris lacinia sapien quis libero.\n#pink",
//        "userAccount" : {
//        "createdAt" : "2023-10-20T09:33:08.029946",
//        "createdBy" : "uno2",
//        "modifiedAt" : "2023-10-20T09:33:08.029946",
//        "modifiedBy" : "uno2",
//        "userId" : "uno2",
//        "userPassword" : "{noop}asdf1234",
//        "email" : "uno2@mail.com",
//        "nickname" : "Uno2",
//        "memo" : "I am Uno2."
//        },
//        "title" : "Quisque ut erat.",
//        "modifiedBy" : "Murial",
//        "createdBy" : "Kamilah",
//        "createdAt" : "2021-05-30T23:53:46",
//        "modifiedAt" : "2021-03-10T08:48:50",
//        "_links" : {
//        "self" : {
//        "href" : "http://localhost:8080/api/articles/1"
//        },
//        "article" : {
//        "href" : "http://localhost:8080/api/articles/1{?projection}",
//        "templated" : true
//        },
//        "userAccount" : {
//        "href" : "http://localhost:8080/api/articles/1/userAccount{?projection}",
//        "templated" : true
//        },
//        "hashtags" : {
//        "href" : "http://localhost:8080/api/articles/1/hashtags"
//        },
//        "articleComments" : {
//        "href" : "http://localhost:8080/api/articles/1/articleComments{?projection}",
//        "templated" : true
//        }
//        }
//        }


// projection 미적용 응답 data
//{
//        "createdAt" : "2021-05-30T23:53:46",
//        "createdBy" : "Kamilah",
//        "modifiedAt" : "2021-03-10T08:48:50",
//        "modifiedBy" : "Murial",
//        "id" : 1,
//        "title" : "Quisque ut erat.",
//        "content" : "Vestibulum quam sapien, varius ut, blandit non, interdum in, ante. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis faucibus accumsan odio. Curabitur convallis.\nDuis consequat dui nec nisi volutpat eleifend. Donec ut dolor. Morbi vel lectus in quam fringilla rhoncus.\nMauris enim leo, rhoncus sed, vestibulum sit amet, cursus id, turpis. Integer aliquet, massa id lobortis convallis, tortor risus dapibus augue, vel accumsan tellus nisi eu orci. Mauris lacinia sapien quis libero.\n#pink",
//        "_embedded" : {
//        "userAccount" : {
//        "userId" : "uno2",
//        "nickname" : "Uno2",
//        "memo" : "I am Uno2.",
//        "email" : "uno2@mail.com",
//        "modifiedBy" : "uno2",
//        "createdBy" : "uno2",
//        "createdAt" : "2023-10-20T09:33:08.029946",
//        "modifiedAt" : "2023-10-20T09:33:08.029946",
//        "_links" : {
//        "self" : {
//        "href" : "http://localhost:8080/api/userAccounts/uno2{?projection}",
//        "templated" : true
//        }
//        }
//        },
//        "articleComments" : [ {
//        "id" : 170,
//        "content" : "Vestibulum quam sapien, varius ut, blandit non, interdum in, ante. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis faucibus accumsan odio. Curabitur convallis.",
//        "userAccount" : {
//        "createdAt" : "2023-10-20T09:33:08.028509",
//        "createdBy" : "uno",
//        "modifiedAt" : "2023-10-20T09:33:08.028509",
//        "modifiedBy" : "uno",
//        "userId" : "uno",
//        "userPassword" : "{noop}asdf1234",
//        "email" : "uno@mail.com",
//        "nickname" : "Uno",
//        "memo" : "I am Uno."
//        },
//        "parentCommentId" : null,
//        "modifiedBy" : "Rouvin",
//        "createdBy" : "Brendan",
//        "createdAt" : "2021-10-28T04:52:36",
//        "modifiedAt" : "2021-12-25T06:43:01",
//        "_links" : {
//        "article" : {
//        "href" : "http://localhost:8080/api/articleComments/170/article{?projection}",
//        "templated" : true
//        },
//        "childComments" : {
//        "href" : "http://localhost:8080/api/articleComments/170/childComments{?projection}",
//        "templated" : true
//        },
//        "userAccount" : {
//        "href" : "http://localhost:8080/api/articleComments/170/userAccount{?projection}",
//        "templated" : true
//        },
//        "self" : {
//        "href" : "http://localhost:8080/api/articleComments/170{?projection}",
//        "templated" : true
//        }
//        }
//        }, {
//        "id" : 173,
//        "content" : "Nulla ut erat id mauris vulputate elementum. Nullam varius. Nulla facilisi.",
//        "userAccount" : {
//        "createdAt" : "2023-10-20T09:33:08.028509",
//        "createdBy" : "uno",
//        "modifiedAt" : "2023-10-20T09:33:08.028509",
//        "modifiedBy" : "uno",
//        "userId" : "uno",
//        "userPassword" : "{noop}asdf1234",
//        "email" : "uno@mail.com",
//        "nickname" : "Uno",
//        "memo" : "I am Uno."
//        },
//        "parentCommentId" : null,
//        "modifiedBy" : "Lou",
//        "createdBy" : "Raimondo",
//        "createdAt" : "2021-06-15T07:11:35",
//        "modifiedAt" : "2021-11-10T07:57:55",
//        "_links" : {
//        "article" : {
//        "href" : "http://localhost:8080/api/articleComments/173/article{?projection}",
//        "templated" : true
//        },
//        "childComments" : {
//        "href" : "http://localhost:8080/api/articleComments/173/childComments{?projection}",
//        "templated" : true
//        },
//        "userAccount" : {
//        "href" : "http://localhost:8080/api/articleComments/173/userAccount{?projection}",
//        "templated" : true
//        },
//        "self" : {
//        "href" : "http://localhost:8080/api/articleComments/173{?projection}",
//        "templated" : true
//        }
//        }
//        }, {
//        "id" : 113,
//        "content" : "Proin eu mi. Nulla ac enim. In tempor, turpis nec euismod scelerisque, quam turpis adipiscing lorem, vitae mattis nibh ligula nec sem.",
//        "userAccount" : {
//        "createdAt" : "2023-10-20T09:33:08.029946",
//        "createdBy" : "uno2",
//        "modifiedAt" : "2023-10-20T09:33:08.029946",
//        "modifiedBy" : "uno2",
//        "userId" : "uno2",
//        "userPassword" : "{noop}asdf1234",
//        "email" : "uno2@mail.com",
//        "nickname" : "Uno2",
//        "memo" : "I am Uno2."
//        },
//        "parentCommentId" : null,
//        "modifiedBy" : "Rolando",
//        "createdBy" : "Torry",
//        "createdAt" : "2021-03-02T11:57:54",
//        "modifiedAt" : "2021-05-09T12:36:08",
//        "_links" : {
//        "article" : {
//        "href" : "http://localhost:8080/api/articleComments/113/article{?projection}",
//        "templated" : true
//        },
//        "childComments" : {
//        "href" : "http://localhost:8080/api/articleComments/113/childComments{?projection}",
//        "templated" : true
//        },
//        "userAccount" : {
//        "href" : "http://localhost:8080/api/articleComments/113/userAccount{?projection}",
//        "templated" : true
//        },
//        "self" : {
//        "href" : "http://localhost:8080/api/articleComments/113{?projection}",
//        "templated" : true
//        }
//        }
//        } ]
//        },
//        "_links" : {
//        "self" : {
//        "href" : "http://localhost:8080/api/articles/1"
//        },
//        "article" : {
//        "href" : "http://localhost:8080/api/articles/1{?projection}",
//        "templated" : true
//        },
//        "userAccount" : {
//        "href" : "http://localhost:8080/api/articles/1/userAccount{?projection}",
//        "templated" : true
//        },
//        "hashtags" : {
//        "href" : "http://localhost:8080/api/articles/1/hashtags"
//        },
//        "articleComments" : {
//        "href" : "http://localhost:8080/api/articles/1/articleComments{?projection}",
//        "templated" : true
//        }
//        }
//        }