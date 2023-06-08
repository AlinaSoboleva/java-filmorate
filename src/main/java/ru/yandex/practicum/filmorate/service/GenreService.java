package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Collection;

public interface GenreService {

    Collection<Genre> findAll();

    Genre getGenreById(int id);
}
