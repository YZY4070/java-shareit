package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

public interface UserService {
    User findById(Long id);

    User save(User user);

    void delete(Long id);
}
