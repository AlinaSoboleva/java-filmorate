package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;

public interface UserService {

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    User getUserById(Integer id);

    void deleteUser(Integer userId);
}
