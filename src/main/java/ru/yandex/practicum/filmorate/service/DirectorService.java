package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.Collection;

public interface DirectorService {
    Collection<Director> findAll();

    Director getDirectorById(int id);

    Director create(Director director);

    Director update(Director director);

    void delete(int id);
}
