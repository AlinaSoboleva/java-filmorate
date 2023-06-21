package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    void create(Film film);

    boolean update(Film film);

    void delete(Film film);

    void validationId(Integer id);

    Collection<Film> getFilms();

    Film getById(Integer id);

    Collection<Film> findAllTopFilms(Integer count);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
}
