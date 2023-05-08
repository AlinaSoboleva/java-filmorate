package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    @Getter(lazy = true)
    private static final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public void create(Film film) {
        film.setId(getId());
        getFilms().put(film.getId(), film);
    }

    @Override
    public boolean update(Film film) {
        if (film.getId() == 0) {
            film.setId(getId());
        }
        if (getFilms().containsKey(film.getId())) {
            getFilms().put(film.getId(), film);
            return true;
        }
        return false;
    }

    @Override
    public void delete(Film film) {
        getFilms().remove(film.getId());
    }

    private int getId() {
        return ++id;
    }

    public static void validationId(Integer id) {
        if (!getFilms().containsKey(id)) {
            throw new FilmIdException(String.format("Фильм с id %s не существует", id));
        }
    }
}
