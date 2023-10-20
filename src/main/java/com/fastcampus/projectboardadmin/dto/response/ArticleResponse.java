package com.fastcampus.projectboardadmin.dto.response;

import com.fastcampus.projectboardadmin.dto.ArticleDto;
import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

// ArticleManagementSerive 에서 REST API 요청로 받은 data 를
// ArticleDto (단건 게시글 )또는 ArticleClientResponse (게시글 리스트) 에 담아서
// controller 로 보내면, controller 에서 여기 ArticleResponse 에 담아서 model 에 보낸다.

// @JsonUnclude : jackson annotation
// Nullable 한 field 를 어떻게 다룰것인가를 정할 수 있다.
// JsonInclude.Include.NON_NULL : Non null field 만 jackson annotation 에 포함시키겠다.
// 해당 조건이 없는 기본 상태에서 만약 명시된 field 값이 없다면 해당 field value 를 null 로 체운다.
// 그런다 해당 조건으로 진행하면 아에 해당 field 를 빼버린다.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArticleResponse(
        Long id,
        UserAccountDto userAccount,
        String title,
        String content,
        LocalDateTime createdAt
) {

    public static ArticleResponse of(
            Long id,
            UserAccountDto userAccount,
            String title,
            String content,
            LocalDateTime createdAt
    ) {
        return new ArticleResponse(id, userAccount, title, content, createdAt);
    }

    public static ArticleResponse withContent(ArticleDto dto) {
        return new ArticleResponse(
                dto.id(),
                dto.userAccount(),
                dto.title(),
                dto.content(),
                dto.createdAt()
        );
    }

    public static ArticleResponse withoutContent(ArticleDto dto) {
        return new ArticleResponse(
                dto.id(),
                dto.userAccount(),
                dto.title(),
                null,
                dto.createdAt()
        );
    }

}