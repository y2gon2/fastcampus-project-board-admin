package com.fastcampus.projectboardadmin.service;

import com.fastcampus.projectboardadmin.dto.ArticleCommentDto;
import com.fastcampus.projectboardadmin.dto.properties.ProjectProperty;
import com.fastcampus.projectboardadmin.dto.response.CommentClientResponse;
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
public class ArticleCommentManagementService {

    private final RestTemplate restTemplate;
    private final ProjectProperty projectProperty;

    public List<ArticleCommentDto> getArticleComments() {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(projectProperty.board().url() + "/api/articleComments")
                .queryParam("size", 10000)
                .build()
                .toUri();

        CommentClientResponse response = restTemplate.getForObject(uri, CommentClientResponse.class);

        return Optional.ofNullable(response)
                .orElseGet(CommentClientResponse::empty)
                .comments();
    }

    public ArticleCommentDto getArticleComment(Long articleCommentId) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(projectProperty.board().url() + "/api/articleComments/" + articleCommentId)
                .build()
                .toUri();

        // jackson 에서 serialize/deserialize 처리
        ArticleCommentDto response = restTemplate.getForObject(uri, ArticleCommentDto.class);

        return Optional.ofNullable(response)
                .orElseThrow(
                        () -> new NoSuchElementException("게시글이 없습니다. - articleCommentId: " + articleCommentId)
                );
    }

    public void deleteArticleComment(Long articleCommentId) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(projectProperty.board().url() + "/api/articleComments/" + articleCommentId)
                .build()
                .toUri();

        restTemplate.delete(uri);
    }
}
