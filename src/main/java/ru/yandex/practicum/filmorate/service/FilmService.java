package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final LikeStorage likeStorage;

    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
    }

    public Film getById(Integer id) {
        filmStorage.validationId(id);
        return filmStorage.getById(id);
    }

    public Collection<Film> findAllTopFilms(Integer count) {
        return filmStorage.findAllTopFilms(count);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.validationId(filmId);
        userStorage.validationId(userId);
        likeStorage.deleteLike(filmId, userId);
    }

    public void putLike(Integer filmId, Integer userId) {
        userStorage.validationId(userId);
        filmStorage.validationId(filmId);
        likeStorage.putLike(filmId, userId);
    }

    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", filmStorage.getFilms().size());
        return filmStorage.getFilms();
    }

    public Film create(Film film) {
        filmStorage.create(film);
        return film;
    }

    public Film update(Film film) {
        if (filmStorage.update(film)) {
            return film;
        }
        log.debug("Фильм с id: {} не найден", film.getId());
        return null;
    }


}
