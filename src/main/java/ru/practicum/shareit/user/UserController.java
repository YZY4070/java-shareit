package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        log.info("Получение пользователя по id - {}", id);
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Получен запрос на создание пользователя - {}", userDto);
        return userService.save(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновления пользователя с ID - {}, данные для обновления: {}", id, userDto);
        userDto.setId(id);
        return userService.save(userDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя с ID: {}", id);
        userService.delete(id);
    }
}
