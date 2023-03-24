package com.github.marceloleite2604.builderandjacksonized;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageFixture {

  public Message create() {
    return Message.builder()
        .user("marcelo-leite")
        .content("Hello from builder-and-jacksonized project!")
        .build();
  }
}
