package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilmService {
    private final Map<Integer, Film> films;
    private int id = 0;

    public FilmService() {
        films = new HashMap<>();
    }

    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    public ResponseEntity<Film> create(Film film) {
        film.setId(getId());
        films.put(film.getId(), film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    public ResponseEntity<Film> update(Film film) {
        if (film.getId() == 0) {
            film.setId(getId());
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        }
        log.debug("Фильм с id: {} не найден", film.getId());
        return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
    }

    private int getId() {
        return ++id;
    }
}
