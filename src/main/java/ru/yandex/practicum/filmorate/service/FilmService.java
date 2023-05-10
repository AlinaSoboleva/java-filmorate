package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.filmStorage = inMemoryFilmStorage;
        this.userStorage = inMemoryUserStorage;
    }

    public Film getFilmById(Integer id) {
        filmStorage.validationId(id);
        return filmStorage.getFilms().get(id);
    }

    public List<Film> findAll(Integer count) {
        List<Film> films = filmStorage.getFilms().values().stream().sorted(new FilmLikesComparator()).collect(Collectors.toList());
        return films.stream().limit(count).collect(Collectors.toList());
    }

    public void deleteLike(Integer id, Integer userId) {
        filmStorage.validationId(id);
        userStorage.validationId(userId);
        filmStorage.getFilms().get(id).getLikes().remove(userId);
    }

    public void putLike(Integer id, Integer userId) {
        userStorage.validationId(userId);
        filmStorage.validationId(id);
        filmStorage.getFilms().get(id).getLikes().add(userId);
    }

    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", filmStorage.getFilms().size());
        return filmStorage.getFilms().values();
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

    static class FilmLikesComparator implements Comparator<Film> {

        @Override
        public int compare(Film o1, Film o2) {
            return o2.getLikes().size() - o1.getLikes().size();
        }
    }
}
