package com.fastcampus.projectboardadmin.dto.response;

import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UserAccountClientResponse(
        @JsonProperty("_embedded") Embedded embedded,
        @JsonProperty("page") Page page
) {
    public static UserAccountClientResponse empty() {
        return new UserAccountClientResponse(
                new Embedded(List.of()),
                new Page(1, 0, 1, 0)
        );
    }

    public static UserAccountClientResponse of(List<UserAccountDto> useraAccounts) {
        return new UserAccountClientResponse(
                new Embedded(useraAccounts),
                new Page(useraAccounts.size(), useraAccounts.size(), 1, 0)
        );
    }

    public record Embedded(List<UserAccountDto> userAccounts) {}

    public record Page(
       int size,
       long totalElements,
       int totalPages,
       int number
    ) {}
}
