package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.DuplicateException;
import ru.practicum.shareit.util.NotFoundException;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.info("Пользователь с таким id = {} не существует!", id);
            return new NotFoundException("Пользователь с таким id = " + id + " не найден!");
        });
    }

    @Override
    public User save(User user) {
        validateEmail(user);
        if (user.getId() == null || userRepository.findById(user.getId()).isEmpty()) {
            log.info("Пользователь сохранён!");
            return userRepository.save(user);
        } else {
            User userFromDB = userRepository.findById(user.getId())
                    .orElseThrow(() -> new NotFoundException("Пользователь с id " + user.getId() + " не найден"));

            User updatedUser = userFromDB.toBuilder()
                    .name(user.getName() != null ? user.getName() : userFromDB.getName())
                    .email(user.getEmail() != null ? user.getEmail() : userFromDB.getEmail())
                    .build();
            log.info("Пользователь обновлен!");
            return userRepository.save(updatedUser);
        }
    }

    @Override
    public void delete(Long id) {
        findById(id);
        log.info("Пользователь обновлён!");
        userRepository.delete(id);
    }


    private void validateEmail(User user) {
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(u -> !u.getId().equals(user.getId()) && u.getEmail().equals(user.getEmail()));
        if (emailExists) {
            log.info("Указанный email {} уже существует", user.getEmail());
            throw new DuplicateException("Указанный email уже существует");
        }
    }
}
