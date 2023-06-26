package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.SearchBy;

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

    Collection<Film> getFilmsByDirectorId(int id, String sortBy);

    List<Film> search(String query, SearchBy by);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> getRecommendations(Integer id);
}