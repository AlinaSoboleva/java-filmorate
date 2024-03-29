package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.SearchBy;
import ru.yandex.practicum.filmorate.model.film.Sort;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film getById(Integer id);

    Collection<Film> findAllTopFilms(Integer count, Integer genreId, Integer year);

    void deleteLike(Integer filmId, Integer userId);

    void putLike(Integer filmId, Integer userId, Integer mark);

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    void deleteFilm(Integer filmId);

    List<Film> getRecommendations(Integer id);

    Collection<Film> getFilmsByDirectorId(int id, Sort sort);

    List<Film> search(String query, List<SearchBy> by);
}
