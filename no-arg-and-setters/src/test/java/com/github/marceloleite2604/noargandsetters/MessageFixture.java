package com.github.marceloleite2604.noargandsetters;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageFixture {

  public Message create() {
    final var message = new Message();
    message.setUser("marcelo-leite");
    message.setContent("Hello from no-args-and-setters project!");
    return message;
  }
}
