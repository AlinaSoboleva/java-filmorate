package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreDao {

    void createGenreByFilm(int genreId, int filmId);

    void deleteAllGenresByFilm(int filmId);

    List<Genre> getGenresByFilm(int filmId);

    Collection<Genre> findAll();

    Genre getById(int id);

    void validationId(Integer id);
}
