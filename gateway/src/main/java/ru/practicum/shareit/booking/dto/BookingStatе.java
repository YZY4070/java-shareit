package ru.practicum.shareit.booking.dto;

public enum BookingStatе {
    ALL,          // Все бронирования
    CURRENT,      // Текущие бронирования
    PAST,         // Завершённые бронирования
    FUTURE,       // Будущие бронирования
    WAITING,      // Ожидающие подтверждения
    REJECTED;

    public static BookingStatе fromString(String value) {
        return BookingStatе.valueOf(value.toUpperCase());
    }
}
