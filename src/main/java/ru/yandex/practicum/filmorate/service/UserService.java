package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Service
public class UserService {

    private final Map<Integer, User> users;
    private int id = 0;

    public UserService() {
        users = new HashMap<>();
    }

    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    public ResponseEntity<User> create(User user) {
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с электронной почтой %s уже зарегистрирован.",
                    user.getEmail()
            ));
        }
        user.setId(getId());
        users.put(user.getId(), user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> update(User user) {
        if (user.getId() == 0) {
            user.setId(getId());
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        log.debug("Пользователь с id: {} не найден", user.getId());
        return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
    }


    private int getId() {
        return ++id;
    }
}
