package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    void create(Film film);

    boolean update(Film film);

    void delete(Film film);
}
