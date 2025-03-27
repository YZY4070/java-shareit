package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestCreateDtoTests {
    @Autowired
    private JacksonTester<ItemRequestCreateDto> jsonItemRequestCreateDto;

    @Test
    public void serialize() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("AllBlack 52")
                .build();

        JsonContent<ItemRequestCreateDto> json = jsonItemRequestCreateDto.write(itemRequestCreateDto);

        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo("AllBlack 52");
    }

    @Test
    public void deserialize() throws Exception {
        String jsonContent = """
                \n{
                    "description":"AllBlack 52"
                }
                """;

        ItemRequestCreateDto itemRequestCreateDto = jsonItemRequestCreateDto.parseObject(jsonContent);

        assertThat(itemRequestCreateDto.getDescription()).isEqualTo("AllBlack 52");
    }

    @Test
    public void validation() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("")
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(itemRequestCreateDto);

        assertThat(violations).hasSize(1);
        assertThat(violations).extracting("message").contains("Описание не может быть пустым");
    }
}
