package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.Exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.Exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.Exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private UserValidator userValidator = new UserValidator();
    private final Map <Integer,User> users = new HashMap<>();
    private int id = 0;

    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        try {
            userValidator.isValidUser(user);
            if (users.containsKey(user.getId())) {
                throw new UserAlreadyExistException(String.format(
                        "Пользователь с электронной почтой %s уже зарегистрирован.",
                        user.getEmail()
                ));
            }
            user.setId(getId());
            users.put(user.getId(), user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (InvalidUserException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> update(@Valid @RequestBody User user) {
        try {
            userValidator.isValidUser(user);
            if (user.getId() == 0) {
                user.setId(getId());
            } else if (users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        } catch (InvalidFilmException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
        log.debug("Пользователь с id: {} не найден", user.getId());
        return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
    }


    private int getId(){
        return ++id;
    }
}
