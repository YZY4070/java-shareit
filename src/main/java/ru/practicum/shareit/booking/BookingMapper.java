package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingRequest bookingRequest, User user, Item item) {
        return Booking.builder()
                .startDate(bookingRequest.getStart())
                .endDate(bookingRequest.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDto toBookingDto(Booking booking, UserDto userDto) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .item(booking.getItem())
                .status(booking.getStatus())
                .booker(userDto)
                .build();
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingInfoDto.builder()
                .id(booking.getId())
                .end(booking.getEndDate())
                .start(booking.getStartDate())
                .build();
    }
}
