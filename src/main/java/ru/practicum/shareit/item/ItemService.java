package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto findById(Long id);

    ItemDto addItem(ItemDto item, Long userId);

    ItemDto updateItem(ItemDto item, Long userId);

    Collection<ItemDto> findItemsByOwnerId(Long ownerId);

    Collection<ItemDto> findItemByText(String text);
}
