package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.Collection;

public interface MpaDao {

    Collection<Mpa> findAll();

    Mpa getById(int id);

    void validationId(Integer id);
}
