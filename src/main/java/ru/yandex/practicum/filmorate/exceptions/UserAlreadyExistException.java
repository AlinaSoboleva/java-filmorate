package ru.yandex.practicum.filmorate.exceptions;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String m) {
        super(m);
    }
}
