package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingRequestDtoTests {
    @Autowired
    private JacksonTester<BookingRequest> jsonBookingRequestDto;

    @Test
    public void serialize() throws Exception {
        BookingRequest bookingRequestDto = BookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2025-03-18T18:30:25"))
                .end(LocalDateTime.parse("2025-03-19T18:30:25"))
                .build();

        JsonContent<BookingRequest> json = jsonBookingRequestDto.write(bookingRequestDto);

        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2025-03-18T18:30:25");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2025-03-19T18:30:25");

    }

    @Test
    public void deserialize() throws Exception {
        String jsonContent = """
                \n{
                    "itemId": 1,
                    "start": "2025-03-18T18:30:25",
                    "end": "2025-03-19T18:30:25"
                }
                """;

        BookingRequest bookingRequestDto = jsonBookingRequestDto.parseObject(jsonContent);

        assertThat(bookingRequestDto.getItemId()).isEqualTo(1);
        assertThat(bookingRequestDto.getStart()).isEqualTo(LocalDateTime.parse("2025-03-18T18:30:25"));
        assertThat(bookingRequestDto.getEnd()).isEqualTo(LocalDateTime.parse("2025-03-19T18:30:25"));

    }

    @Test
    public void validation() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        BookingRequest bookingRequestDto = BookingRequest.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(bookingRequestDto);

        assertThat(violations).hasSize(3);
        assertThat(violations).extracting("message").contains("ID не может быть пустым");
        assertThat(violations).extracting("message")
                .contains("Дата начала бронирования не может быть пустым");
        assertThat(violations).extracting("message")
                .contains("Дата окончания бронирования не может быть пустым");
    }

    @Test
    public void validationDates() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        BookingRequest bookingRequestDto = BookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2025-03-15T18:30:25"))
                .end(LocalDateTime.parse("2025-03-15T18:30:25"))
                .build();

        Set<ConstraintViolation<BookingRequest>> violations = validator.validate(bookingRequestDto);

        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
                .contains("Дата начала бронирования не может быть в прошлом");
        assertThat(violations).extracting("message")
                .contains("Дата окончания бронирования не может быть в прошлом");
    }
}
