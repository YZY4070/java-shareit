package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

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
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        Item item = ItemMapper.toItem(itemDto, ownerId);
        checkUser(item.getOwnerId());
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto item, Long userId) {
        findById(item.getId());
        checkUser(userId);
        Item itemFromDB = itemRepository.findById(item.getId()).get();
        Item updatedItem = itemFromDB.toBuilder()
                .name(item.getName() != null ? item.getName() : itemFromDB.getName())
                .description(item.getDescription() != null ? item.getDescription() : itemFromDB.getDescription())
                .available(item.getAvailable() != null ? item.getAvailable() : itemFromDB.getAvailable())
                .build();
        return ItemMapper.toDto(itemRepository.save(updatedItem));
    }

    @Override
    public Collection<ItemDto> findItemsByOwnerId(Long ownerId) {
        userRepository.findById(ownerId);
        return itemRepository.findItemsByOwnerId(ownerId).stream().map(ItemMapper::toDto).collect(Collectors.toList());///
    }

    @Override
    public Collection<ItemDto> findItemByText(String text) {
        return itemRepository.findItemByText(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }


    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });
    }
}
