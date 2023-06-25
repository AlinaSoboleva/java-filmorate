package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.List;

public interface DirectorStorage {

    Collection<Director> findAll();

    Director getDirectorById(int id);

    Director create(Director director);

    Boolean update(Director director);

    void delete(int id);

    void validationId(Integer id);

    List<Director> getDirectorsByFilm(int filmId);

    void setDirectorsInDb(Film film);

    void deleteDirectorByFilm(Film film);
}
