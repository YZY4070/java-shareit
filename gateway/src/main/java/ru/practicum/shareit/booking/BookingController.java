package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingStatе;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> setApprove(@PathVariable long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return bookingClient.setApprove(bookingId, ownerId, approved);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingRequest newBookingRequest,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.createBooking(userId, newBookingRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookingByUserIdAndState(@RequestParam(defaultValue = "ALL") String state,
                                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        BookingStatе stateParam = BookingStatе.fromString(state);
        log.info("Get booking with state {}, userId={}", state, userId);
        return bookingClient.getBookingByUserIdAndState(userId, stateParam);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookingByOwnerIdAndState(@RequestParam(defaultValue = "ALL") String state,
                                                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        BookingStatе stateParam = BookingStatе.fromString(state);
        log.info("Get owner bookings with state {}, ownerId={}", stateParam, ownerId);
        return bookingClient.getBookingByOwnerIdAndState(ownerId, stateParam);
    }
}
