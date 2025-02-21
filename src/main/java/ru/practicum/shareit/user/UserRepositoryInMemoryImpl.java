package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryInMemoryImpl implements UserRepository {
    public static Long getNext = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            user.setId(getNextId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }


    private Long getNextId() {
        return getNext++;
    }

}
