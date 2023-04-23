package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.Exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
 @Slf4j
public class FilmValidator {

    public boolean isValidFilm(Film film){
        if (!isValidName(film)){
            throw new InvalidFilmException("Название фильма не может быть пустым");
        }
        if (!isValidDescription(film)){
            throw new InvalidFilmException("Описание фильма слишком длинное");
        }
        if (!isValidReleaseData(film)){
            throw new InvalidFilmException("Дата релиза  не может быть  раньше 28 декабря 1895 года");
        }
        if (!isValidDuration(film)){
            throw new InvalidFilmException("Продолжительность фильма не может быть отрицательной");
        }
        return true;
    }

    private boolean isValidName(Film film){
        return !film.getName().isBlank();
    }

    private boolean isValidDescription(Film film){
        return film.getDescription().length()<200;
    }

    private   boolean isValidReleaseData(Film film){
        LocalDate date = LocalDate.of(1895,12,27);
        return film.getReleaseDate().isAfter(date);
    }

    private   boolean isValidDuration(Film film){
        return film.getDuration() > 0;
    }
}
