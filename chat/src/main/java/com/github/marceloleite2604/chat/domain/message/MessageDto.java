package com.github.marceloleite2604.chat.domain.message;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MessageDto {

    private String id;

    private Long time;

    @NotBlank
    private String user;

    @NotBlank
    private String content;
}
