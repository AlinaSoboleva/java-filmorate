package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikeDao likeStorage;
    private final DirectorStorage directorStorage;
    private final EventFeedService eventFeedService;

    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           @Qualifier("userDbStorage") UserStorage userStorage,
                           @Qualifier("filmLikeStorage") FilmLikeDao likeStorage,
                           EventFeedService eventFeedService,
                           DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.directorStorage = directorStorage;
        this.eventFeedService = eventFeedService;
    }

    @Override
    public Film getById(Integer id) {
        filmStorage.validationId(id);
        return filmStorage.getById(id);
    }

    @Override
    public Collection<Film> findAllTopFilms(Integer count) {
        return filmStorage.findAllTopFilms(count);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.validationId(filmId);
        userStorage.validationId(userId);
        likeStorage.deleteLike(filmId, userId);
        eventFeedService.saveEvent(EventType.LIKE,
                EventOperation.REMOVE,
                userId,
                filmId);
    }

    @Override
    public void putLike(Integer filmId, Integer userId) {
        userStorage.validationId(userId);
        filmStorage.validationId(filmId);
        likeStorage.putLike(filmId, userId);
        eventFeedService.saveEvent(EventType.LIKE,
                EventOperation.ADD,
                userId,
                filmId);
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

    @Override
    public Collection<Film> getFilmsByDirectorId(int id, String sortBy) {
        directorStorage.validationId(id);
        return filmStorage.getFilmsByDirectorId(id, sortBy);
    }

    @Override
    public List<Film> search(String query, String by) {
        return filmStorage.search(query, by);
    }
}
