package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTests {
    @Autowired
    private JacksonTester<UserDto> jsonUserCreateRequestDto;

    @Test
    public void serialize() throws Exception {
        UserDto userCreateRequestDto = UserDto.builder()
                .name("GUNNA")
                .email("gunna@test.com")
                .build();

        JsonContent<UserDto> json = jsonUserCreateRequestDto.write(userCreateRequestDto);

        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("GUNNA");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("gunna@test.com");
    }

    @Test
    public void deserialize() throws Exception {
        String jsonContent = """
                    \n{
                        "name": "GUNNA",
                        "email": "gunna@test.com"
                    }
                """;

        UserDto userDto = jsonUserCreateRequestDto.parseObject(jsonContent);

        assertThat(userDto.getName()).isEqualTo("GUNNA");
        assertThat(userDto.getEmail()).isEqualTo("gunna@test.com");
    }

    @Test
    public void validation() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        UserDto userDto = UserDto.builder()
                .name("")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message").contains(
                "Имя пользователя не может быть пустым",
                "email пользователя не может быть пустым"
        );
    }
}
