package com.fastcampus.projectboardadmin.dto.response;

import com.fastcampus.projectboardadmin.dto.ArticleCommentDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CommentClientResponse(
    @JsonProperty("_embedded") Embedded enbedded,
    @JsonProperty("page") Page page
) {

    public static CommentClientResponse empty() {
        return new CommentClientResponse(
                new Embedded(List.of()),
                new Page(1, 0, 1, 0)
        );
    }

    public static CommentClientResponse of(List<ArticleCommentDto> comments) {
        return new CommentClientResponse(
                new Embedded(comments),
                new Page(comments.size(), comments.size(), 1, 0)
        );
    }

    public record Embedded(List<ArticleCommentDto> comments) {}

    public record Page(
            int size,
            long totalElements,
            int totalPages,
            int number
    ) {}
}
