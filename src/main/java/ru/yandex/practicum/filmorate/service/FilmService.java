package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.filmStorage = inMemoryFilmStorage;
    }

    public Film getFilmById(Integer id) {
        InMemoryFilmStorage.validationId(id);
        return InMemoryFilmStorage.getFilms().get(id);
    }

    public List<Film> findAll(Integer count) {
        List<Film> films = InMemoryFilmStorage.getFilms().values().stream().sorted(new FilmLikesComparator()).collect(Collectors.toList());
        return films.stream().limit(count).collect(Collectors.toList());
    }

    public void deleteLike(Integer id, Integer userId) {
        InMemoryFilmStorage.validationId(id);
        InMemoryUserStorage.validationId(userId);
        InMemoryFilmStorage.getFilms().get(id).getLikes().remove(userId);
    }

    public void putLike(Integer id, Integer userId) {
        InMemoryUserStorage.validationId(userId);
        InMemoryFilmStorage.validationId(id);
        InMemoryFilmStorage.getFilms().get(id).getLikes().add(userId);
    }

    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", InMemoryFilmStorage.getFilms().size());
        return InMemoryFilmStorage.getFilms().values();
    }

    public ResponseEntity<Film> create(Film film) {
        filmStorage.create(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    public ResponseEntity<Film> update(Film film) {

        if (filmStorage.update(film)) {
            return new ResponseEntity<>(film, HttpStatus.OK);
        }
        log.debug("Фильм с id: {} не найден", film.getId());
        return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
    }

    static class FilmLikesComparator implements Comparator<Film> {

        @Override
        public int compare(Film o1, Film o2) {
            return o2.getLikes().size() - o1.getLikes().size();
        }
    }
}
