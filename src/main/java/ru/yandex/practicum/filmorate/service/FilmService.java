package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;

public interface FilmService {

    Film getById(Integer id);

    Collection<Film> findAllTopFilms(Integer count);

    void deleteLike(Integer filmId, Integer userId);

    void putLike(Integer filmId, Integer userId);

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Collection<Film> search(String query, String by);
}
