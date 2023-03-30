package com.github.marceloleite2604.noargandsetters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ChatServiceIT {

  @Autowired
  private ChatService chatService;

  @Test
  void shouldDeserializeMessageProperly() {

    final var message = MessageFixture.create();

    final var persistedMessage = chatService.saveAndRetrieve(message);

    assertThat(persistedMessage).isNotNull()
        .usingRecursiveComparison()
        .ignoringFields("id", "time")
        .isEqualTo(message);

    assertThat(persistedMessage.getId()).isNotNull();
    assertThat(persistedMessage.getTime()).isPositive();

    final var updatedContent = "This is my updated message.";

    persistedMessage.setContent(updatedContent);

    final var updatedMessage = chatService.updateAndRetrieve(persistedMessage);

    assertThat(updatedMessage).isNotNull()
        .usingRecursiveComparison()
        .ignoringFields("time")
        .isEqualTo(persistedMessage);

    assertThat(updatedMessage.getTime()).isGreaterThan(persistedMessage.getTime());
  }

  @SpringBootConfiguration
  @ComponentScan("com.github.marceloleite2604.builderandjacksonized")
  @ConfigurationPropertiesScan
  public static class ITConfiguration {
  }

}
