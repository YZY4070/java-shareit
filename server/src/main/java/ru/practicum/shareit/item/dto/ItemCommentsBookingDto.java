package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.comments.CommentDto;

import java.util.Collection;

@Data
@Builder(toBuilder = true)
public class ItemCommentsBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Collection<CommentDto> comments;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
}
