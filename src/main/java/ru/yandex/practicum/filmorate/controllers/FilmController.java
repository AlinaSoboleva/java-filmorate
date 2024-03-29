package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.SearchBy;
import ru.yandex.practicum.filmorate.model.film.Sort;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable Integer id,
                        @PathVariable Integer userId,
                        @RequestParam(value = "mark", required = false, defaultValue = "10") Integer mark) {
        filmService.putLike(id, userId, mark);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Integer id) {
        return new ResponseEntity<>(filmService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public Collection<Film> findAll(@RequestParam(value = "count", defaultValue = "10", required = false)
                                    @Positive(message = "Некорректное значение count") Integer count,
                                    @RequestParam(value = "genreId", required = false)
                                    @Positive(message = "Некорректное значение count") Integer genreId,
                                    @RequestParam(value = "year", required = false)
                                    @Positive(message = "Некорректное значение count") Integer year) {
        return filmService.findAllTopFilms(count, genreId, year);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public ResponseEntity<Film> create(@Valid @RequestBody Film film) {
        return new ResponseEntity<>(filmService.create(film), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Film> update(@Valid @RequestBody Film film) {
        Film updatedFilm = filmService.update(film);
        if (updatedFilm == null) {
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirectorId(
            @PathVariable Integer directorId,
            @RequestParam(value = "sortBy", defaultValue = "year") Sort sort) {
        return filmService.getFilmsByDirectorId(directorId, sort);
    }

    @GetMapping("/search")
    public Collection<Film> search(@RequestParam(required = false) String query,
                                   @RequestParam(required = false) List<SearchBy> by) {
        return filmService.search(query, by);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable("filmId") Integer filmId) {
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/common")
    public ResponseEntity<List<Film>> getCommonFilms(@RequestParam("userId") Integer userId,
                                                     @RequestParam("friendId") Integer friendId) {
        return ResponseEntity.ok(filmService.getCommonFilms(userId, friendId));
    }
}
