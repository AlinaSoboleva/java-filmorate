package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.impl.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.*;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final LikeStorage likeStorage;

    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           UserDbStorage userStorage,
                           LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
    }

    @Override
    public Film getById(Integer id) {
        filmStorage.validationId(id);
        return filmStorage.getById(id);
    }

    @Override // Не придумал ничего лучше, чем разбить на несколько методов в DAO
    public Collection<Film> findAllTopFilms(Integer count, Integer genreId, Integer year) {
        if (genreId == null && year == null) {
            return filmStorage.findTopFilms(count);
        }
        else if (genreId == null) {
            return filmStorage.findAllTopIfYear(count, year);
        } else if (year == null) {
            return filmStorage.findAllTopIfGenre(count, genreId);
        }
        return filmStorage.findAllTopFilms(count, genreId, year);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.validationId(filmId);
        userStorage.validationId(userId);
        likeStorage.deleteLike(filmId, userId);
    }

    @Override
    public void putLike(Integer filmId, Integer userId) {
        userStorage.validationId(userId);
        filmStorage.validationId(filmId);
        likeStorage.putLike(filmId, userId);
    }

    @Override
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", filmStorage.getFilms().size());
        return filmStorage.getFilms();
    }

    @Override
    public Film create(Film film) {
        filmStorage.create(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (filmStorage.update(film)) {
            return film;
        }
        log.debug("Фильм с id: {} не найден", film.getId());
        return null;
    }

    @Override
    public List<Film> getRecommendations(Integer id) {
        log.debug("Получены рекомендации фильмов по id: {}", id);
        return filmStorage.getRecommendations(id);
    }

    @Override
    public void deleteFilm(Integer filmId) {
        filmStorage.delete(filmId);
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}
