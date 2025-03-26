package ru.practicum.shareit.booking.dto;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static BookingStatus fromString(String value) {
        return BookingStatus.valueOf(value.toUpperCase());
    }
}
