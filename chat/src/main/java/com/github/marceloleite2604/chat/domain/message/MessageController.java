package com.github.marceloleite2604.chat.domain.message;

import com.github.marceloleite2604.chat.validation.ValidUuid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = MessageController.BASE_PATH)
@RequiredArgsConstructor
public class MessageController {

    public static final String BASE_PATH = "messages";

    private final MessageService messageService;

    private final MessageToDtoMapper messageToDtoMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> post(@RequestBody @Validated MessageDto messageDto) {
        final var message = messageToDtoMapper.mapFrom(messageDto);

        final var persistedMessage = messageService.save(message);

        final var location = createMessageLocationUri(persistedMessage.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location)
                .build();

    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<MessageDto> get(@PathVariable @ValidUuid String id) {

        final var uuid = UUID.fromString(id);

        return messageService.findById(uuid)
                .map(messageToDtoMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound()
                        .build());
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getAll() {

        final var messages = messageService.findAll();

        if (CollectionUtils.isEmpty(messages)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
        }

        final var messagesDto = messageToDtoMapper.mapAllTo(messages)
                .stream()
                .sorted(Comparator.comparing(MessageDto::getTime))
                .toList();

        return ResponseEntity.ok(messagesDto);
    }

    private String createMessageLocationUri(UUID id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .pathSegment("{id}")
                .buildAndExpand(id)
                .toUri()
                .toASCIIString();
    }
}
