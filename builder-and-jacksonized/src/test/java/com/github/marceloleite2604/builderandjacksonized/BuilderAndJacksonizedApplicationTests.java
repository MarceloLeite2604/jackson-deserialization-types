package com.github.marceloleite2604.builderandjacksonized;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class BuilderAndJacksonizedApplicationTests {

  private WebClient webClient;

  @BeforeEach
  void setUp() {
    webClient = WebClient.builder()
        .baseUrl("http://localhost:8082/chat")
        .build();
  }

  @Test
  void shouldDeserializeMessageProperly() {

    final var message = MessageFixture.create();

    final var id = putMessage(message);

    final var persistedMessage = getMessage(id);

    assertThat(persistedMessage).isNotNull()
        .usingRecursiveComparison()
        .ignoringFields("id", "time")
        .isEqualTo(message);

    assertThat(persistedMessage.getId()).isNotNull();
    assertThat(persistedMessage.getTime()).isPositive();

    final var updatedContent = "This is my updated message.";

    persistedMessage.setContent(updatedContent);

    postMessage(persistedMessage, persistedMessage.getId());

    final var updatedMessage = getMessage(id);

    assertThat(updatedMessage).isNotNull()
        .usingRecursiveComparison()
        .ignoringFields("time")
        .isEqualTo(persistedMessage);

    assertThat(updatedMessage.getTime()).isGreaterThan(persistedMessage.getTime());
  }

  private Message getMessage(String messageId) {
    return webClient.get()
        .uri("/messages/{id}", messageId)
        .retrieve()
        .bodyToMono(Message.class)
        .block();
  }

  private String putMessage(Message message) {
    return webClient.put()
        .uri("/messages")
        .bodyValue(message)
        .exchangeToMono(this::retrieveMessageIdFromResponse)
        .block();
  }

  private void postMessage(Message message, String id) {
    webClient.post()
        .uri("/messages/{id}", id)
        .bodyValue(message)
        .retrieve()
        .toBodilessEntity()
        .block();
  }

  private Mono<String> retrieveMessageIdFromResponse(ClientResponse clientResponse) {

    if (!HttpStatus.CREATED.equals(clientResponse.statusCode())) {
      return Mono.error(new IllegalStateException("Unexpected HTTP response: " + clientResponse.statusCode()));
    }

    return Optional.of(clientResponse)
        .flatMap(this::retrieveLocation)
        .flatMap(this::retrieveMessageId)
        .map(Mono::just)
        .orElseGet(() -> Mono.error(new IllegalStateException("Could not retrieve message ID from HTTP response.")));
  }

  private Optional<String> retrieveLocation(ClientResponse clientResponse) {
    try {
      final var locationHeaders = clientResponse.headers()
          .header("Location");

      return Optional.of(locationHeaders.iterator()
          .next());
    } catch (NoSuchElementException exception) {
      log.error("Could not find \"Location\" header on response.");
      return Optional.empty();
    }
  }

  private Optional<String> retrieveMessageId(String location) {
    try {
      return Optional.of(location.substring(location.lastIndexOf("/") + 1));
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      log.error("Could not find message ID on \"{}\".", location);
      return Optional.empty();
    }
  }
}
