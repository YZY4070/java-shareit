package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comments.*;
import ru.practicum.shareit.item.dto.ItemCommentsBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemCommentsBookingDto findById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> {
            log.info("Вещь с id = {} не найдена!", id);
            return new NotFoundException("Вещь не найдена!");
        });
        Collection<CommentDto> commentsDto = commentRepository.searchCommentsByItemId(id).stream()
                .map(CommentMapper::toDto)
                .toList();

        Booking lastBooking = bookingRepository.findLastBookingByItemId(id);
        Booking nextBooking = bookingRepository.findNextBookingByItemId(id);

        BookingInfoDto lastBookingDto = (lastBooking != null) ? BookingMapper.toBookingInfoDto(lastBooking) : null;
        BookingInfoDto nextBookingDto = (nextBooking != null) ? BookingMapper.toBookingInfoDto(nextBooking) : null;

        return ItemMapper.toSuperDto(item, commentsDto, lastBookingDto, nextBookingDto);
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        checkUser(ownerId);
        Item item = ItemMapper.toItem(itemDto, userRepository.findById(ownerId).get());
        checkUser(item.getOwner().getId());
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
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findItemByText(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentRequestDto request) {
        checkUser(userId);
        checkItem(itemId);
        Booking booking = bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.APPROVED).stream()
                .findFirst().orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getEndDate().isAfter(LocalDateTime.now()))
            throw new BadRequestException("Нельзя оставить комментарий, пока не закончилось бронирование");

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setItemId(itemId);
        comment.setAuthor(userRepository.findById(userId).get());
        comment.setCreated(LocalDateTime.now());

        commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }


    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("Пользователь с id = {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден");
                });
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id: " + itemId + " не найден"));
    }
}
