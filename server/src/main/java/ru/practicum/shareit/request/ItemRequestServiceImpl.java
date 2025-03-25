package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestorResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequestResponseDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User user = findUserById(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(user, itemRequestCreateDto);
        ItemRequest savedRequest = itemRequestRepository.save(request);
        return ItemRequestMapper.toItemRequestResponseDto(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponseDto findById(Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с вещью id = " + requestId + "не найден!"));
        Collection<Item> items = itemRepository.findAllByRequestId(requestId);
        Collection<ItemForRequestorResponseDto> itemsDto = items.stream()
                .map(ItemMapper::toItemForRequestorResponseDto)
                .toList();
        return ItemRequestMapper.toItemRequestResponseDto(request, itemsDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestResponseDto> findAll(Long userId) {
        findUserById(userId);
        return itemRequestRepository.findAllByRequesterIdIsNot(userId).stream()
                .map(ItemRequestMapper::toItemRequestResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestResponseDto> findAllByUser(Long userId) {
        findUserById(userId);
        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdOrderByCreated(userId);
        Collection<Long> requestsIds = requests.stream().map(ItemRequest::getId).toList();
        Collection<Item> itemsDb = itemRepository.findAllByRequestIdIn(requestsIds);
        Map<Long, List<Item>> requestedItems = itemsDb.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
        return requests.stream()
                .map(itemRequest -> {
                    Collection<Item> items = requestedItems.getOrDefault(itemRequest.getId(), Collections.emptyList());
                    Collection<ItemForRequestorResponseDto> itemsDto = items.stream()
                            .map(ItemMapper::toItemForRequestorResponseDto)
                            .toList();
                    return ItemRequestMapper.toItemRequestResponseDto(itemRequest, itemsDto);
                })
                .toList();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
