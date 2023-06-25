package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.Exceptions.DirectorIdException;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.Exceptions.ReviewsIdException;
import ru.yandex.practicum.filmorate.Exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = {UserIdException.class, FilmIdException.class, DirectorIdException.class, ReviewsIdException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleId(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
