package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto findByid(@PathVariable Long id) {
        log.info("Запрос на поиск вещи");
        return itemService.findById(id);
    }

    @GetMapping
    public Collection<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на поиск вещей пользователя ID - {}", ownerId);
        return itemService.findItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByText(@RequestParam(name = "text", required = false) String text) {
        log.info("Поиск вещи по тексту: {}", text);
        return itemService.findItemByText(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на доавбление нового предмета от пользователя с Id - {}", ownerId);
        log.info("Данные вещи: {}", itemDto);
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long id,
                          @RequestBody ItemDto itemDto) {
        log.info("Зарос на обновление вещи от пользователя с Id - {}", ownerId);
        log.info("Данные вещи: {}", itemDto);
        itemDto.setId(id);
        return itemService.updateItem(itemDto, ownerId);
    }
}
