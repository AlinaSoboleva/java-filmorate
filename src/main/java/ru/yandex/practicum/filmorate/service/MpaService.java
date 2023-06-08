package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.util.Collection;

public interface MpaService {

    Collection<Mpa> findAll();

    Mpa getMpaById(int id);
}
