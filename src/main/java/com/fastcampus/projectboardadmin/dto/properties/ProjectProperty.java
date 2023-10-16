package com.fastcampus.projectboardadmin.dto.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
* 관리자 프로젝트 전용 property
*
* @param board 게시판 관련 property
*/
@ConfigurationProperties("project")
public record ProjectProperty(Board board) {

    /**
     * 게시판 관련 property
     *
     * @param url 게시판 서비스 호스트명
     */
    public record Board(String url) {}
}
