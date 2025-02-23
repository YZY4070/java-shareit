package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> findById(Long id);

    Item save(Item item);

    Collection<Item> findItemsByOwnerId(Long userId);

    Collection<Item> findItemByText(String text);

}
