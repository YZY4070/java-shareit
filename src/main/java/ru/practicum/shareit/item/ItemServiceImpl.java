package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto findById(Long id) {
        return ItemMapper.toDto(itemRepository.findById(id).orElseThrow(() -> {
            log.info("Вещь с id = {} не найдена!", id);
            return new NotFoundException("Вещь не найдена!");
        }));
    }

    @Override
    public Item save(Item item) {
        checkUser(item.getOwnerId());
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item) {
        checkUser(item.getOwnerId());
        Item itemFromDB = itemRepository.findById(item.getId()).get();
        Item updatedItem = itemFromDB.toBuilder()
                .name(item.getName() != null ? item.getName() : itemFromDB.getName())
                .description(item.getDescription() != null ? item.getDescription() : itemFromDB.getDescription())
                .available(item.getAvailable() != null ? item.getAvailable() : itemFromDB.getAvailable())
                .build();
        return itemRepository.save(updatedItem);
    }

    @Override
    public Collection<Item> findItemsByOwnerId(Long userId) {
        userRepository.findById(userId);
        return itemRepository.findItemsByOwnerId(userId);
    }

    @Override
    public Collection<Item> findItemByText(String text) {
        return itemRepository.findItemByText(text);
    }


    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });
    }
}
