package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Override
    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", userStorage.getUsers().size());
        return userStorage.getUsers();
    }

    @Override
    public User create(User user) {
        userStorage.create(user);
        return user;
    }

    @Override
    public User update(User user) {
        if (userStorage.update(user)) {
            return user;
        }
        log.debug("Пользователь с id: {} не найден", user.getId());
        return null;

    }

    @Override
    public User getUserById(Integer id) {
        return userStorage.getById(id);
    }

    @Override
    public void deleteUser(Integer userId) {
        userStorage.delete(userId);
    }
}
