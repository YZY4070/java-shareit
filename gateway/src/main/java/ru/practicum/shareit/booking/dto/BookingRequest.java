package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingRequest {

    @NotNull(message = "Дата начала бронирования не может быть пустым")
    @Future(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть пустым")
    @Future(message = "Дата окончания бронирования не может быть в прошлом")
    private LocalDateTime end;

    @NotNull(message = "ID не может быть пустым")
    private Long itemId;
}
