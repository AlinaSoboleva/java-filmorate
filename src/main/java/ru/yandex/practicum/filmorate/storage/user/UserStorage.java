package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;

public interface UserStorage {

    void create(User user);

    boolean update(User user);

    void delete(User user);

    User getById(Integer id);


    void validationId(Integer id);

    Collection<User> getUsers();

}
