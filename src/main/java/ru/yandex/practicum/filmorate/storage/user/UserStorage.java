package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserStorage {
    void create(User user);

    boolean update(User user);

    void delete(User user);

    User getById(Integer id);

    List<User> getFriends(Set<Integer> friendsId);

    void validationId(Integer id);

    Map<Integer, User> getUsers();
}
