package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestCreateDto itemRequestCreateDto) {

        log.info("Запрос от пользователя: {}. Данные запроса: {}", userId, itemRequestCreateDto);
        return itemRequestService.create(userId, itemRequestCreateDto);
    }

    @GetMapping("/all")
    public Collection<ItemRequestResponseDto> findAllWithoutUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("запрос на поиск запрошенных вещей");
        return itemRequestService.findAll(userId);
    }

    @GetMapping
    public Collection<ItemRequestResponseDto> findAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на поиск запрошенных вещей пользователя с ID: {}", userId);
        return itemRequestService.findAllByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findById(@PathVariable long requestId) {
        log.info("Получен запрос на поиск запроса вещей с ID: {}", requestId);
        return itemRequestService.findById(requestId);
    }
}
