package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemDto findById(Long id);

    Item save(Item item);

    Item updateItem(Item item);

    Collection<Item> findItemsByOwnerId(Long userId);

    Collection<Item> findItemByText(String text);
}
