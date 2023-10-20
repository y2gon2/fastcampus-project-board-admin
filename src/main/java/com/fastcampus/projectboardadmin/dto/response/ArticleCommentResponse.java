package com.fastcampus.projectboardadmin.dto.response;

import com.fastcampus.projectboardadmin.dto.ArticleCommentDto;
import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

// 화면에서 보여줄 data 에 대한 field 만으로 구성
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArticleCommentResponse(
        Long id,
        UserAccountDto userAccount,
        String content,
        LocalDateTime createdAt
) {
    public static ArticleCommentResponse of(
            Long id,
            UserAccountDto userAccount,
            String content,
            LocalDateTime createdAt
    ) {
        return new ArticleCommentResponse(
                id,
                userAccount,
                content,
                createdAt
        );
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        return ArticleCommentResponse.of(
                dto.id(),
                dto.userAccount(),
                dto.content(),
                dto.createdAt()
        );
    }
}
