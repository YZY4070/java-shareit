package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCommentsBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemCommentsBookingDto findById(Long id);

    ItemDto addItem(ItemDto item, Long userId);

    ItemDto updateItem(ItemDto item, Long userId);

    Collection<ItemDto> findItemsByOwnerId(Long ownerId);

    Collection<ItemDto> findItemByText(String text);

    CommentDto createComment(Long itemId, Long userId, CommentRequestDto request);
}
