package ru.yandex.practicum.filmorate.exceptions;

public class UserIdException extends RuntimeException {
    public UserIdException(String message) {
        super(message);
    }
}
