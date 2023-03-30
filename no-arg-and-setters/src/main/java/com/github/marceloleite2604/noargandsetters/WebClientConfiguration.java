package com.github.marceloleite2604.noargandsetters;

import com.github.marceloleite2604.noargandsetters.properties.ChatProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfiguration {

  @Bean
  public WebClient createChatWebClient(ChatProperties chatProperties) {
    return WebClient.builder()
        .baseUrl(chatProperties.getBaseUrl())
        .build();
  }
}
