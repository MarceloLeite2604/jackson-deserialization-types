package com.github.marceloleite2604.noargandsetters;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Message {

  private String id;

  private long time;

  private String user;

  private String content;
}
