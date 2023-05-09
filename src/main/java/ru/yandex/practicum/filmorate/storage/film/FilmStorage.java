package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    void create(Film film);

    boolean update(Film film);

    void delete(Film film);

    void validationId(Integer id);

    Map<Integer, Film> getFilms();
}
