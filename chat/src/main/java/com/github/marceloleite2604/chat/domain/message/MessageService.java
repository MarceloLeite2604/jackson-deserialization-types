package com.github.marceloleite2604.chat.domain.message;

import com.github.marceloleite2604.chat.domain.message.Message;
import com.github.marceloleite2604.chat.domain.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;

  public Message save(Message message) {

    final var messageToBePersisted = message.toBuilder()
        .id(UUID.randomUUID())
        .time(LocalDateTime.now())
        .build();

    return messageRepository.save(messageToBePersisted);
  }

  public Optional<Message> findById(UUID id) {
    return messageRepository.findById(id);
  }

  public Collection<Message> findAll() {
    return messageRepository.findAll();
  }

  public Optional<Message> update(UUID id, Message updatedMessage) {
    return messageRepository.findById(id)
        .map(persistedMessage -> merge(persistedMessage, updatedMessage))
        .map(messageRepository::save);
  }

  private Message merge(Message persistedMessage, Message updatedMessage) {
    return persistedMessage.toBuilder()
        .content(updatedMessage.getContent())
        .time(LocalDateTime.now())
        .build();
  }
}
