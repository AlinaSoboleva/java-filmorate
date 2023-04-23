package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.Exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilmService {
    private final FilmValidator filmValidator = new FilmValidator();
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    public ResponseEntity<?> create(@RequestBody Film film) {
        try {
            filmValidator.isValidFilm(film);
            film.setId(getId());
            films.put(film.getId(), film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (InvalidFilmException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> update(@RequestBody Film film) {
        try {
            filmValidator.isValidFilm(film);
            if (film.getId() == 0) {
                film.setId(getId());
            } else if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                return new ResponseEntity<>(film, HttpStatus.OK);
            }
        } catch (InvalidFilmException e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
        log.debug("Фильм с id: {} не найден", film.getId());
        return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
    }

    private int getId() {
        return ++id;
    }
}
