package com.fastcampus.projectboardadmin.dto.response;

import com.fastcampus.projectboardadmin.dto.ArticleDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record ArticleClientResponse(
        @JsonProperty("_embedded") Embedded embedded,
        @JsonProperty("page") Page page

        // 게시판 project 접속 localhost:8080/api 를 통해 hal explorer 에 접속하면,
        // 게시판 글 요청 response body 의 json 을 보면
        // "_embedded" key 에 articles List 정보가 있으며
        // "page" key 전체 정보 size 관련 정보가 담겨있다.
        // 해당 정보를 parsing 해서 가져올 수 있도록 설정한다.
) {

    public static ArticleClientResponse empty() {
        return new ArticleClientResponse(
                new Embedded(List.of()),
                new Page(1, 0, 1, 0)
        );
    }

    public static ArticleClientResponse of(List<ArticleDto> articles) {
        return new ArticleClientResponse(
                new Embedded(articles),
                new Page(articles.size(), articles.size(), 1, 0)
        );
    }

    public List<ArticleDto> articles() { return this.embedded.articles(); }

    public record Embedded(List<ArticleDto> articles) {}

    public record Page(
       int size,
       long totalElements,
       int totalPages,
       int number
    ) {}
}
