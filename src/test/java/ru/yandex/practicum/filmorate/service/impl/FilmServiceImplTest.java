package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceImplTest extends BaseTest {

    private final FilmService filmService;
    private final UserService userService;
    private final DirectorService directorService;
    private final MpaService mpaService;


    @Test
    void whenThereIsOnlyOneFilmWithTitle() {
        List<Film> films = filmService.search("OLD", "title");
        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0).getId()).isEqualTo(EXISTING_FILM_5_ID);
        assertThat(films.get(0).getName()).isEqualTo(EXISTING_FILM_5_NAME);
    }

    @Test
    void whenThereIsOnlyOneFilmWithDirector() {
        Film film = new Film("NewFilm", "Desc", LocalDate.of(2023, 06, 22), 180);
        Director director = directorService.getDirectorById(1);
        Mpa mpa = mpaService.getMpaById(1);
        List<Director> directors = new ArrayList<>();
        directors.add(0, director);
        film.setDirectors(directors);
        film.setMpa(mpa);
        filmService.create(film);
        List<Film> films = filmService.search("DIR", "director");
        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0).getDirectors().get(0).getName()).isEqualTo("DIRECTOR");
    }

    @Test
    void whenReturnMorePopularFilm() {
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_USER_ID);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID);
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_USER_ID);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID);
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_USER_ID);
        List<Film> films = filmService.search("Fi", "title");

        assertThat(films.size()).isEqualTo(4);
        assertThat(films.get(0).getId()).isEqualTo(EXISTING_FILM_3_ID);
    }
}