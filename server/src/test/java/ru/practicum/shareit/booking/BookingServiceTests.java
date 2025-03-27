package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class BookingServiceTests {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User user2;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Snow");
        user.setEmail("johnsnow@test.com");
        userRepository.save(user);

        user2 = new User();
        user2.setName("John Wick");
        user2.setEmail("johnwick@test.com");
        userRepository.save(user2);

        item = new Item();
        item.setName("Test Item John Snow");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        booking = new Booking();
        booking.setStartDate(LocalDateTime.of(2025, 3, 11, 12, 0, 0));
        booking.setEndDate(LocalDateTime.of(2025, 3, 12, 12, 0, 0));
        booking.setItem(item);
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
    }

    @Test
    void findBookingById_shouldReturnBookingDto() {
        BookingDto result = bookingService.findBookingById(booking.getId(), user2.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStartDate(), result.getStart());
        assertEquals(booking.getEndDate(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void createBooking_shouldReturnBookingDto() {
        BookingRequest newBookingRequest = new BookingRequest();
        newBookingRequest.setItemId(item.getId());
        newBookingRequest.setStart(LocalDateTime.of(2025, 5, 11, 12, 0, 0));
        newBookingRequest.setEnd(LocalDateTime.of(2025, 5, 12, 12, 0, 0));

        BookingDto result = bookingService.createBooking(newBookingRequest, user2.getId());

        assertNotNull(result);
        assertEquals(newBookingRequest.getStart(), result.getStart());
        assertEquals(newBookingRequest.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void createBooking_shouldThrowBadRequestException_whenStartDateIsAfterEndDate() {
        BookingRequest newBookingRequest = new BookingRequest();
        newBookingRequest.setItemId(item.getId());
        newBookingRequest.setStart(LocalDateTime.of(2025, 5, 12, 12, 0, 0));
        newBookingRequest.setEnd(LocalDateTime.of(2025, 5, 11, 12, 0, 0));

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(newBookingRequest, user2.getId()));
    }

    @Test
    void setApprove_shouldReturnBookingDto_WhenApproved() {
        BookingDto result = bookingService.setApprove(booking.getId(), user.getId(), true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void setApprove_shouldReturnBookingDto_WhenRejected() {
        BookingDto result = bookingService.setApprove(booking.getId(), user.getId(), false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void setApprove_shouldThrowForbiddenException_whenUserIsNotOwner() {
        assertThrows(ForbiddenException.class, () -> bookingService.setApprove(booking.getId(), user2.getId(), true));
    }

    @Test
    void findAllBookingsByUserIdAndState_shouldReturnBookings() {
        Collection<BookingDto> result = bookingService.findAllBookingsByUserIdAndState(user2.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.iterator().next().getId());
    }

    @Test
    void findAllBookingsByUserIdAndState_shouldReturnEmpty_whenNoBookingsMatchState() {
        Collection<BookingDto> result = bookingService.findAllBookingsByUserIdAndState(user2.getId(), "FUTURE");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllBookingsByOwnerIdAndState_shouldReturnBookings() {
        Collection<BookingDto> result = bookingService.findAllBookingsByOwnerIdAndState(user.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.iterator().next().getId());
    }
}
