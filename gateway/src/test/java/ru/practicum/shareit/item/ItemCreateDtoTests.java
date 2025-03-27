package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemCreateDtoTests {
    @Autowired
    private JacksonTester<ItemDto> jsonItemCreateRequestDto;

    @Test
    public void serialize() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("rofls")
                .description("Test description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDto> json = jsonItemCreateRequestDto.write(itemDto);

        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("rofls");
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Test description");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(json).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    public void deserialize() throws Exception {
        String jsonContent = """
                    \n{
                        "name": "rofls",
                        "description": "Test description",
                        "available": true,
                        "requestId": 1
                    }
                """;

        ItemDto itemCreateRequestDto = jsonItemCreateRequestDto.parseObject(jsonContent);

        assertThat(itemCreateRequestDto.getName()).isEqualTo("rofls");
        assertThat(itemCreateRequestDto.getDescription()).isEqualTo("Test description");
        assertThat(itemCreateRequestDto.getAvailable()).isEqualTo(true);
        assertThat(itemCreateRequestDto.getRequestId()).isEqualTo(1);
    }

    @Test
    public void validation() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        ItemDto commentRequestDto = ItemDto.builder()
                .name("")
                .description("")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(commentRequestDto);

        assertThat(violations).hasSize(3);
        assertThat(violations).extracting("message").contains("Название не должно быть пустым");
        assertThat(violations).extracting("message").contains("Описание не должно быть пустым");
        assertThat(violations).extracting("message").contains("Наличие не должно быть null");
    }
}
