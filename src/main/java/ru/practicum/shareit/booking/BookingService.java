package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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
        checkUser(userId);
        if (state.equals("ALL"))
            return bookingRepository.findBookingsByBookerId(userId).stream()
                    .map(booking -> BookingMapper.toBookingDto(
                            booking,
                            UserMapper.toUserDto(booking.getBooker()))
                    )
                    .collect(Collectors.toList());
        else {
            BookingStatus status = BookingStatus.valueOf(state);
            return bookingRepository.findBookingsByBookerIdAndStatus(userId, status).stream()
                    .map(booking -> BookingMapper.toBookingDto(
                            booking,
                            UserMapper.toUserDto(booking.getBooker()))
                    )
                    .collect(Collectors.toList());
        }

    }


    public Collection<BookingDto> findAllBookingsByOwnerIdAndState(Long ownerId, String state) {

        checkUser(ownerId);
        if (state.equals("ALL"))
            return bookingRepository.findBookingsByItem_Owner_Id(ownerId).stream()
                    .map(booking -> BookingMapper.toBookingDto(
                            booking,
                            UserMapper.toUserDto(booking.getBooker()))
                    )
                    .collect(Collectors.toList());
        else {
            BookingStatus status = BookingStatus.valueOf(state);
            return bookingRepository.findBookingsByItem_Owner_IdAndStatus(ownerId, status).stream()
                    .map(booking -> BookingMapper.toBookingDto(
                            booking,
                            UserMapper.toUserDto(booking.getBooker()))
                    )
                    .collect(Collectors.toList());
        }
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
