package com.github.marceloleite2604.builderandjacksonized;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Jacksonized
@Getter
@Setter
public class Message {

  private final String id;

  private final long time;

  private final String user;

  private String content;
}
