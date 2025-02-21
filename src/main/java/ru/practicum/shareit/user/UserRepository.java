package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    User save(User user);

    void delete(Long id);

    Collection<User> findAll();
}
