package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    void create(Film film);

    boolean update(Film film);

    void delete(Integer filmId);

    void validationId(Integer id);

    Collection<Film> getFilms();

    Film getById(Integer id);

    Collection<Film> findAllTopFilms(Integer count);

    Collection<Film> getFilmsByDirectorId(int id, String sortBy);

    List<Film> search(String query, String by);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getRecommendations(Integer id);
}