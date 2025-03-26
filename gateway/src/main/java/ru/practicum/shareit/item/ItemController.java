package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> findByid(@PathVariable Long id) {
        log.info("Запрос на поиск вещи");
        return itemClient.findById(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на поиск вещей пользователя ID - {}", ownerId);
        return itemClient.findAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByText(@RequestParam(name = "text", required = false) String text) {
        log.info("Поиск вещи по тексту: {}", text);
        return itemClient.findItemsByText(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на доавбление нового предмета от пользователя с Id - {}", ownerId);
        log.info("Данные вещи: {}", itemDto);
        return itemClient.create(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @PathVariable Long id,
                                         @RequestBody ItemDto itemDto) {
        log.info("Зарос на обновление вещи от пользователя с Id - {}", ownerId);
        log.info("Данные вещи: {}", itemDto);
        itemDto.setId(id); //??????
        return itemClient.update(ownerId, id, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createComment(@PathVariable long itemId,
                                                @RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody CommentRequestDto request) {
        log.info("Adding comment to itemId={}, userId={}, comment={}", itemId, userId, request);
        return itemClient.createComment(userId, itemId, request);
    }

}
