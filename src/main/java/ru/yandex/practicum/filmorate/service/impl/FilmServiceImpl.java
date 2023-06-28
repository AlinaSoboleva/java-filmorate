package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.SearchBy;
import ru.yandex.practicum.filmorate.model.film.Sort;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    @Qualifier("filmLikeStorage")
    private final FilmLikeDao likeStorage;
    private final DirectorStorage directorStorage;
    private final EventFeedService eventFeedService;

    @Override
    public Film getById(Integer id) {
        filmStorage.validationId(id);
        return filmStorage.getById(id);
    }

    @Override // Не придумал ничего лучше, чем разбить на несколько методов в DAO
    public Collection<Film> findAllTopFilms(Integer count, Integer genreId, Integer year) {
        if (genreId == null && year == null) {
            return filmStorage.findTopFilms(count);
        } else if (genreId == null) {
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
        eventFeedService.saveEvent(EventType.LIKE,
                EventOperation.REMOVE,
                userId,
                filmId);
    }

    @Override
    public void putLike(Integer filmId, Integer userId, Integer mark) {
        userStorage.validationId(userId);
        filmStorage.validationId(filmId);
        likeStorage.putLike(filmId, userId, mark);
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
    public Collection<Film> getFilmsByDirectorId(int id, Sort sort) {
        directorStorage.validationId(id);
        return filmStorage.getFilmsByDirectorId(id, sort);
    }

    @Override
    public List<Film> search(String query, List<SearchBy> by) {
        return filmStorage.search(query, by);
    }
}
