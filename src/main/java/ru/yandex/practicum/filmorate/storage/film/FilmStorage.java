package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.SearchBy;
import ru.yandex.practicum.filmorate.model.film.Sort;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    void create(Film film);

    boolean update(Film film);

    void delete(Integer filmId);

    void validationId(Integer id);

    Collection<Film> getFilms();

    Film getById(Integer id);

    Collection<Film> findAllTopFilms(Integer count, Integer genreId, Integer year);

    Collection<Film> findAllTopIfGenre(Integer count, Integer genreId);

    Collection<Film> findAllTopIfYear(Integer count, Integer year);

    Collection<Film> findTopFilms(Integer count);

    Collection<Film> getFilmsByDirectorId(int id, Sort sort);

    List<Film> search(String query, List<SearchBy> by);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getRecommendations(Integer id);
}