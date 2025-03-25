package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestResponseDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto);

    ItemRequestResponseDto findById(Long requestId);

    Collection<ItemRequestResponseDto> findAll(Long userId);

    Collection<ItemRequestResponseDto> findAllByUser(Long userId);
}
