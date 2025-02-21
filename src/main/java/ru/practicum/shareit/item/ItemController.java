package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto findByid(@PathVariable Long id) {
        log.info("Запрос на поиск вещи");
        Item foundItem = itemService.findById(id);
        return ItemMapper.toDto(foundItem);
    }

    @GetMapping
    public Collection<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос на поиск вещей пользователя ID - {}", ownerId);
        Collection<Item> foundItems = itemService.findItemsByOwnerId(ownerId);
        Collection<ItemDto> foundItemsDto = foundItems.stream()
                .map(ItemMapper::toDto)
                .toList();
        return foundItemsDto;
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByText(@RequestParam(name = "text", required = false) String text) {
        log.info("Поиск вещи по тексту: {}", text);
        Collection<Item> foundItems = itemService.findItemByText(text);
        Collection<ItemDto> foundItemsDto = foundItems.stream()
                .map(ItemMapper::toDto)
                .toList();
        return foundItemsDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на доавбление нового предмета от пользователя с Id - {}", ownerId);
        log.info("Данные вещи: {}", itemDto);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        Item createdItem = itemService.save(item);
        return ItemMapper.toDto(createdItem);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long id,
                          @RequestBody ItemDto itemDto) {
        log.info("Зарос на обновление вещи от пользователя с Id - {}", ownerId);
        log.info("Данные вещи: {}", itemDto);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        Item updatedItem = itemService.updateItem(item.toBuilder().id(id).build());
        return ItemMapper.toDto(updatedItem);
    }
}
