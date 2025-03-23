package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingInfoDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
}
