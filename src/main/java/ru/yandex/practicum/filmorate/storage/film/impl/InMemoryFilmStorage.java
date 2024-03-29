package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.SearchBy;
import ru.yandex.practicum.filmorate.model.film.Sort;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Collection<Film> findAllTopFilms(Integer count, Integer genreId, Integer year) {
        List<Film> films = getFilms().stream().sorted(new FilmLikesComparator()).collect(Collectors.toList());
        return films.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public Collection<Film> findAllTopIfGenre(Integer count, Integer genreId) {
        return null;
    }

    @Override
    public Collection<Film> findAllTopIfYear(Integer count, Integer year) {
        return null;
    }

    @Override
    public Collection<Film> findTopFilms(Integer count) {
        return null;
    }

    @Override
    public Collection<Film> getFilmsByDirectorId(int id, Sort sort) {
        return null;
    }

    @Override
    public List<Film> search(String query, List<SearchBy> by) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public List<Film> getRecommendations(Integer id) {
        return null;
    }

    @Override
    public Film getById(Integer id) {
        return films.get(id);
    }

    @Override
    public void create(Film film) {
        film.setId(getId());
        films.put(film.getId(), film);
    }

    @Override
    public boolean update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return true;
        }
        return false;
    }

    @Override
    public void delete(Integer filmId) {
        getFilms().removeIf(f -> f.getId() == filmId);
    }

    private int getId() {
        return ++id;
    }

    public void validationId(Integer id) {
        if (!films.containsKey(id)) {
            throw new FilmIdException(String.format("Фильм с id %s не существует", id));
        }
    }

    static class FilmLikesComparator implements Comparator<Film> {

        @Override
        public int compare(Film o1, Film o2) {
            return Double.compare(o1.getRate(), o2.getRate());
        }
    }
}
