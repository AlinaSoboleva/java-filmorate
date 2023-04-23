package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService){
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll(){
        return filmService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Film film) {
        return  filmService.create(film);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Film film) {
        return filmService.update(film);
    }
}
