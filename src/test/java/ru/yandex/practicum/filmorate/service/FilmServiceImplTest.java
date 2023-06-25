package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.Exceptions.DirectorIdException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceImplTest extends BaseTest {

    private final FilmService filmService;
    private final DirectorStorage directorStorage;

    @Test
    void whenRemoveDirector_getFilmsByDirectorId_returnNotFound() {
        List<Director> directors = new ArrayList<>();
        directors.add(directorStorage.getDirectorById(EXISTING_DIRECTOR_ID));
        Film film = filmService.getById(EXISTING_FILM_ID);
        film.setDirectors(directors);
        directorStorage.delete(EXISTING_DIRECTOR_ID);

        assertThrows(
                DirectorIdException.class,
                () -> filmService.getFilmsByDirectorId(EXISTING_DIRECTOR_ID, SORT_BY_YEAR)
        );

    }

    @Test
    void getFilmsByDirectorId_returnSortByYear() {
        List<Director> directors = new ArrayList<>();
        directors.add(directorStorage.getDirectorById(EXISTING_DIRECTOR_ID));
        Film film = filmService.getById(EXISTING_FILM_ID);
        Film film2 = filmService.getById(EXISTING_FILM_2_ID);
        film.setDirectors(directors);
        film2.setDirectors(directors);
        filmService.update(film);
        filmService.update(film2);

        List<Film> films = new ArrayList<>(filmService.getFilmsByDirectorId(EXISTING_DIRECTOR_ID, SORT_BY_YEAR));

        assertThat(films.size()).isEqualTo(2);
        assertThat(films.get(0).getId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(films.get(1).getId()).isEqualTo(EXISTING_FILM_2_ID);
    }
}