package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    public BookingDto findBookingById(Long bookingId, Long userId) {
        User user = checkUser(userId);
        Booking booking = checkBooking(bookingId);
        if (booking.getBooker().getId().equals(userId) ||
                booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking, UserMapper.toUserDto(user));
        } else throw new ForbiddenException("У пользователя нет доступа к бронированию");
    }


    @Transactional
    public BookingDto setApprove(Long bookingId, Long ownerId, Boolean approve) {
        Booking booking = checkBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId))
            throw new ForbiddenException("Пользователь не является владельцем бронирования");
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);

        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker()));
    }


    public BookingDto createBooking(BookingRequest newBookingRequest, Long userId) {
        LocalDateTime endDate = newBookingRequest.getEnd();
        LocalDateTime startDate = newBookingRequest.getStart();

        if (endDate.isEqual(startDate) || endDate.isBefore(startDate)) {
            throw new BadRequestException("Время начала бронирования (" +
                    newBookingRequest.getStart() + ") "
                    + "не может быть равным или больше времени окончания ("
                    + newBookingRequest.getEnd() + ")");
        }
        User booker = checkUser(userId);
        Item item = itemRepository.findById(newBookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Item  c id " + newBookingRequest.getItemId() + " не найден"));

        if (!item.getAvailable())
            throw new BadRequestException("Item " + item.getId() + " недоступен для бронирования");

        Booking booking = BookingMapper.toBooking(newBookingRequest, booker, item);
        booking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booker));
    }


    public Collection<BookingDto> findAllBookingsByUserIdAndState(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case "CURRENT" ->
                    bookingRepository.findBookingsByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId, now, now, sort)
                            .stream()
                            .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                            .collect(Collectors.toList());
            case "PAST" -> bookingRepository.findBookingsByBookerIdAndEndDateIsBefore(userId, now, sort)
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                    .collect(Collectors.toList());
            case "FUTURE" -> bookingRepository.findBookingsByBookerIdAndStartDateIsAfter(userId, now, sort)
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                    .collect(Collectors.toList());
            case "WAITING", "REJECTED" ->
                    bookingRepository.findBookingsByBookerIdAndStatus(userId, BookingStatus.valueOf(state), sort)
                            .stream()
                            .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                            .collect(Collectors.toList());
            default -> bookingRepository.findBookingsByBookerId(userId, sort)
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                    .collect(Collectors.toList());
        };
    }

    public Collection<BookingDto> findAllBookingsByOwnerIdAndState(Long ownerId, String state) {
        checkUser(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case "CURRENT" ->
                    bookingRepository.findByItem_Owner_IdAndStartDateIsBeforeAndEndDateIsAfter(ownerId, now, now, sort)
                            .stream()
                            .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                            .collect(Collectors.toList());
            case "PAST" -> bookingRepository.findByItem_Owner_IdAndEndDateIsBefore(ownerId, now, sort)
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                    .collect(Collectors.toList());
            case "FUTURE" -> bookingRepository.findByItem_Owner_IdAndStartDateIsAfter(ownerId, now, sort)
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                    .collect(Collectors.toList());
            case "WAITING", "REJECTED" ->
                    bookingRepository.findBookingsByItem_Owner_IdAndStatus(ownerId, BookingStatus.valueOf(state), sort)
                            .stream()
                            .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                            .collect(Collectors.toList());
            default -> bookingRepository.findBookingsByItem_Owner_Id(ownerId, sort)
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking, UserMapper.toUserDto(booking.getBooker())))
                    .collect(Collectors.toList());
        };
    }

    private Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking с id: " + bookingId + " не найден"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User с id: " + userId + " не найден"));
    }
}
