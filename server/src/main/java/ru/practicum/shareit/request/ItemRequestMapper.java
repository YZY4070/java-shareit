package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForRequestorResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@UtilityClass
public class ItemRequestMapper {
    ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest, Collection<ItemForRequestorResponseDto> items) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    ItemRequest toItemRequest(User user, ItemRequestCreateDto itemRequestCreateDto) {
        return ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
    }
}
