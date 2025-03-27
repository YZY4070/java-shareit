package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class UserServiceTests {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("John Wick")
                .email("johnwick@test.com")
                .build();
        userRepository.save(user);
    }

    @Test
    void findById_shouldReturnUserDto() {
        UserDto result = userService.findById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void findById_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    void save_shouldCreateNewUser() {
        UserDto newUserDto = UserDto.builder()
                .name("John Wick 2")
                .email("johnwick2@test.com")
                .build();

        UserDto result = userService.save(newUserDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(newUserDto.getName(), result.getName());
        assertEquals(newUserDto.getEmail(), result.getEmail());
    }

    @Test
    void save_shouldUpdateExistingUser() {
        UserDto updatedUserDto = UserDto.builder()
                .id(user.getId())
                .name("John Updated")
                .email("johnwick.updated@test.com")
                .build();

        UserDto result = userService.save(updatedUserDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(updatedUserDto.getName(), result.getName());
        assertEquals(updatedUserDto.getEmail(), result.getEmail());
    }

    @Test
    void save_shouldThrowDuplicateException_whenEmailExists() {
        UserDto newUserDto = UserDto.builder()
                .name("John Wick 2")
                .email("johnwick@test.com") // Дублирующий email
                .build();

        assertThrows(DuplicateException.class, () -> userService.save(newUserDto));
    }

    @Test
    void save_shouldUpdateWithPartialData() {
        UserDto partialUpdateDto = UserDto.builder()
                .id(user.getId())
                .name("John Updated")
                .build(); // Оставляем email null

        UserDto result = userService.save(partialUpdateDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("John Updated", result.getName());
        assertEquals(user.getEmail(), result.getEmail()); // Email должен остаться прежним
    }

    @Test
    void delete_shouldRemoveUser() {
        Long userId = user.getId();

        userService.delete(userId);

        assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void delete_shouldThrowNotFoundException_whenUserDoesNotExist() {
        assertThrows(NotFoundException.class, () -> userService.delete(999L));
    }
}