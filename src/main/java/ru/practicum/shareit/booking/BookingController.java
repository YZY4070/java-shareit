package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto setApprove(@PathVariable long bookingId,
                                 @RequestParam boolean approved,
                                 @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return bookingService.setApprove(bookingId, ownerId, approved);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public BookingDto createBooking(@RequestBody @Valid BookingRequest newBookingRequest,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(newBookingRequest, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDto> getBookingByUserIdAndState(@RequestParam(defaultValue = "ALL") String state,
                                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findAllBookingsByUserIdAndState(userId, state);
    }


    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingDto> getBookingByOwnerIdAndState(@RequestParam(defaultValue = "ALL") String state,
                                                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return bookingService.findAllBookingsByOwnerIdAndState(ownerId, state);
    }
}
