package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.Exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    public List<User> getFriendsList(Set<Integer> friendsId) {
        return getFriends(friendsId);
    }

    public List<User> getFriends(Set<Integer> friendsId) {
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsId) {
            friends.add(getById(id));
        }
        return friends;
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public void create(User user) {
        for (User u : getUsers()) {
            if (u.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException(String.format(
                        "Пользователь с электронной почтой %s уже зарегистрирован.",
                        user.getEmail()
                ));
            }
        }
        user.setId(getId());
        users.put(user.getId(), user);
    }

    @Override
    public boolean update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return true;
        }
        return false;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public User getById(Integer id) {
        validationId(id);
        return users.get(id);
    }

    private int getId() {
        return ++id;
    }

    public void validationId(Integer id) {
        if (!users.containsKey(id)) {
            throw new UserIdException(String.format("Пользователь с id %s не существует", id));
        }
    }
}
