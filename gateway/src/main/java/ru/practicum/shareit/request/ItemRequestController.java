package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestCreateDto itemRequestCreateDto) {

        log.info("Запрос от пользователя: {}. Данные запроса: {}", userId, itemRequestCreateDto);
        return itemRequestClient.create(userId, itemRequestCreateDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllWithoutUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("запрос на поиск запрошенных вещей");
        return itemRequestClient.findAllWithoutUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на поиск запрошенных вещей пользователя с ID: {}", userId);
        return itemRequestClient.findAllForUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable long requestId) {
        log.info("Получен запрос на поиск запроса вещей с ID: {}", requestId);
        return itemRequestClient.findById(requestId);
    }
}
