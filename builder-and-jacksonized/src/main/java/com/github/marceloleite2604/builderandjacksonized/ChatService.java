package com.github.marceloleite2604.builderandjacksonized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

  private final WebClient webClient;

  public Message saveAndRetrieve(Message message) {
    final var id = save(message);
    return retrieveMandatory(id);
  }

  public String save(Message message) {
    Assert.notNull(message, "Message must not be null.");

    return webClient.put()
        .uri(uriBuilder -> uriBuilder.pathSegment(Paths.MESSAGES)
            .build())
        .bodyValue(message)
        .exchangeToMono(this::retrieveMessageIdFromResponse)
        .block();
  }

  public Optional<Message> retrieve(String id) {
    Assert.notNull(id, "Message ID must not be null.");

    return webClient.get()
        .uri(uriBuilder -> uriBuilder.pathSegment(Paths.MESSAGES, Paths.ID)
            .build(id))
        .retrieve()
        .bodyToMono(Message.class)
        .map(Optional::of)
        .onErrorResume(throwable -> {
          log.error("Exception thrown while trying to retrieve message with ID \"{}\".", id);
          return Mono.just(Optional.empty());
        })
        .block();
  }

  public Message retrieveMandatory(String id) {
    return retrieve(id).orElseThrow(() -> {
      final var message = String.format("Could not find message with ID \"%s\".", id);
      return new IllegalStateException(message);
    });
  }

  public Message updateAndRetrieve(Message message) {
    Assert.notNull(message, "Message must not be null.");

    update(message);

    return retrieveMandatory(message.getId());
  }

  public void update(Message message) {
    Assert.notNull(message, "Message must not be null.");

    webClient.post()
        .uri(uriBuilder -> uriBuilder.pathSegment(Paths.MESSAGES, Paths.ID)
            .build(message.getId()))
        .bodyValue(message)
        .retrieve()
        .toBodilessEntity()
        .block();
  }

  private Mono<String> retrieveMessageIdFromResponse(ClientResponse clientResponse) {

    if (!HttpStatus.CREATED.equals(clientResponse.statusCode())) {
      final var message = String.format("Unexpected HTTP response: %s", clientResponse.statusCode());
      return Mono.error(new IllegalStateException(message));
    }

    return Optional.of(clientResponse)
        .map(this::retrieveLocation)
        .map(this::retrieveId)
        .map(Mono::just)
        .orElseGet(() -> Mono.error(new IllegalStateException("Could not retrieve message ID from HTTP response.")));
  }

  private String retrieveLocation(ClientResponse clientResponse) {
    try {
      final var locations = clientResponse.headers()
          .header(HttpHeaders.LOCATION);

      return locations.iterator()
          .next();
    } catch (NoSuchElementException exception) {
      final var message = String.format("Exception thrown when trying to retrieve \"%s\" header from HTTP response.", HttpHeaders.LOCATION);
      throw new IllegalStateException(message, exception);
    }
  }

  private String retrieveId(String location) {
    try {
      return location.substring(location.lastIndexOf("/") + 1);
    } catch (IndexOutOfBoundsException exception) {
      final var message = String.format("Exception thrown when trying to retrieve message ID from \"%s\".", location);
      throw new IllegalStateException(message, exception);
    }
  }

  private static class Paths {
    public static final String MESSAGES = "messages";
    public static final String ID = "{id}";
  }
}
