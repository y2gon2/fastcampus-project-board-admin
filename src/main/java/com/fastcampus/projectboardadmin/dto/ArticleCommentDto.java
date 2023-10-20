package com.fastcampus.projectboardadmin.dto;

import com.fastcampus.projectboardadmin.dto.UserAccountDto;

import java.time.LocalDateTime;

public record ArticleCommentDto(
        Long id,
        Long articleId,
        UserAccountDto userAccount,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public ArticleCommentDto of(
            Long articleId,
            UserAccountDto userAccount,
            Long parentCommentId,
            String content
    ) {
        return new ArticleCommentDto(
                null,
                articleId,
                userAccount,
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
            UserAccountDto userAccount,
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
                userAccount,
                parentCommentId,
                content,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy
        );
    }

}
