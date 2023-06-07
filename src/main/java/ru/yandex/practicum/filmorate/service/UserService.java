package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", userStorage.getUsers().size());
        return userStorage.getUsers();
    }

    public User create(User user) {
        userStorage.create(user);
        return user;
    }

    public User update(User user) {
        if (userStorage.update(user)) {
            return user;
            //return new ResponseEntity<>(user, HttpStatus.OK);
        }
        log.debug("Пользователь с id: {} не найден", user.getId());
        return null;

    }

    public User getUserById(Integer id) {
        return userStorage.getById(id);
    }
}
