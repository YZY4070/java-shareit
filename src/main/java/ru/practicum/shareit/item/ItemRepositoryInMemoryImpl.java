package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {
    private static Long getNext = 0L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null || !items.containsKey(item.getId())) {
            item.setId(getNextId());
        }
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Collection<Item> findItemsByOwnerId(Long userId) {
        return items.values().stream().filter(item -> item.getOwnerId()
                        .equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> findItemByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String lowerCaseText = text.toLowerCase();

        return items.values().stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(lowerCaseText)
                                || item.getDescription().toLowerCase().contains(lowerCaseText))
                                && item.getAvailable()).collect(Collectors.toList());
    }

    private Long getNextId() {
        return getNext++;
    }
}
