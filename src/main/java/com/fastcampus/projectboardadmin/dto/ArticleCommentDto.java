package com.fastcampus.projectboardadmin.dto;

import java.time.LocalDateTime;

public record ArticleCommentDto(
        Long id,
        Long articleId,
        UserAccountDto userAccountDto,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public ArticleCommentDto of(
            Long articleId,
            UserAccountDto userAccountDto,
            Long parentCommentId,
            String content
    ) {
        return new ArticleCommentDto(
                null,
                articleId,
                userAccountDto,
                parentCommentId,
                content,
                null,
                null,
                null,
                null
        );
    }

    public static ArticleCommentDto of(
            Long id,
            Long articleId,
            UserAccountDto userAccountDto,
            Long parentCommentId,
            String content,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy
    ) {
        return new ArticleCommentDto(
                id,
                articleId,
                userAccountDto,
                parentCommentId,
                content,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy
        );
    }

}
