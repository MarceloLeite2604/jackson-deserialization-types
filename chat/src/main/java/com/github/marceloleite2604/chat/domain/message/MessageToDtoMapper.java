package com.github.marceloleite2604.chat.domain.message;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class MessageToDtoMapper {

    public static final ZoneOffset STANDARD_ZONE_OFFSET = ZoneOffset.UTC;

    public MessageDto mapTo(Message message) {
        final var id = message.getId()
                .toString();

        final var time = message.getTime()
                .toEpochSecond(STANDARD_ZONE_OFFSET);

        return MessageDto.builder()
                .id(id)
                .time(time)
                .user(message.getUser())
                .content(message.getContent())
                .build();
    }

    public Message mapFrom(MessageDto messageDto) {
        final var id = Optional.ofNullable(messageDto.getId())
                .map(UUID::fromString)
                .orElse(null);

        final var time = Optional.ofNullable(messageDto.getTime())
                .map(mappedTime -> Instant.ofEpochMilli(mappedTime)
                        .atZone(STANDARD_ZONE_OFFSET)
                        .toLocalDateTime())
                .orElse(null);

        return Message.builder()
                .id(id)
                .time(time)
                .user(messageDto.getUser())
                .content(messageDto.getContent())
                .build();
    }

    public Collection<MessageDto> mapAllTo(Collection<Message> messages) {
        return messages.stream()
                .map(this::mapTo)
                .toList();
    }
}
