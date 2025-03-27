package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class BookingRepositoryTests {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        booker = userRepository.save(new User(null, "John Doe", "john@example.com"));
        owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        item = itemRepository.save(new Item(null, "Bike", "Mountain bike", true, owner, null));
    }

    @Test
    void findBookingsByBookerId_ShouldReturnBookings() {
        Booking booking = bookingRepository.save(new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED));
        Collection<Booking> bookings = bookingRepository.findBookingsByBookerId(booker.getId(), Sort.by(Sort.Direction.DESC, "startDate"));

        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsByBookerIdAndStartDateIsBeforeAndEndDateIsAfter_ShouldReturnCurrentBookings() {
        Booking booking = bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED));
        Collection<Booking> bookings = bookingRepository.findBookingsByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(booker.getId(), LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "startDate"));

        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsByBookerIdAndEndDateIsBefore_ShouldReturnPastBookings() {
        Booking booking = bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.APPROVED));
        Collection<Booking> bookings = bookingRepository.findBookingsByBookerIdAndEndDateIsBefore(booker.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "startDate"));

        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsByBookerIdAndStartDateIsAfter_ShouldReturnFutureBookings() {
        Booking booking = bookingRepository.save(new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED));
        Collection<Booking> bookings = bookingRepository.findBookingsByBookerIdAndStartDateIsAfter(booker.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "startDate"));

        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsByBookerIdAndStatus_ShouldReturnBookingsByStatus() {
        Booking booking = bookingRepository.save(new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING));
        Collection<Booking> bookings = bookingRepository.findBookingsByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, Sort.by(Sort.Direction.ASC, "startDate"));

        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsByItem_Owner_Id_ShouldReturnOwnerBookings() {
        Booking booking = bookingRepository.save(new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED));
        Collection<Booking> bookings = bookingRepository.findBookingsByItem_Owner_Id(owner.getId(), Sort.by(Sort.Direction.DESC, "startDate"));

        assertThat(bookings).hasSize(1).contains(booking);
    }
}
