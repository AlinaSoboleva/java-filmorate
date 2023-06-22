package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceImplTest extends BaseTest {

    private final FilmService filmService;
    private final UserService userService;

    @Test
    void whenUsersAddLikeToCommonFilm_getCommonFilms_returnsFilm() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID);

        List<Film> commonFilms = filmService.getCommonFilms(EXISTING_USER_ID, EXISTING_FRIEND_ID);
        assertThat(commonFilms.size()).isEqualTo(1);
        assertThat(commonFilms.get(0).getId()).isEqualTo(EXISTING_FILM_ID);
    }

    @Test
    void whenUsersAddLikesToSeveralFilms_getCommonFilms_returnsFilmsSortedByRate() {
        User created = userService.create(
                new User("email@email.com",
                        "login",
                        "name",
                        LocalDate.of(1990, 10, 10))
        );
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID);
        filmService.putLike(EXISTING_FILM_ID, created.getId());
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_USER_ID);
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID);

        List<Film> commonFilms = filmService.getCommonFilms(EXISTING_USER_ID, EXISTING_FRIEND_ID);
        assertThat(commonFilms.size()).isEqualTo(2);
        assertThat(commonFilms.get(0).getId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(commonFilms.get(1).getId()).isEqualTo(EXISTING_FILM_2_ID);
    }

    @Test
    void whenNoCommonFilms_getCommonFilmsReturnsEmptyList() {
        List<Film> commonFilms = filmService.getCommonFilms(EXISTING_USER_ID, EXISTING_FRIEND_ID);
        assertThat(commonFilms).isEmpty();
    }

    @Test
    void whenRemoveCommonLikeFromFilm_getCommonFilms_doesnt_includeFilm() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID);
        filmService.deleteLike(EXISTING_FILM_ID, EXISTING_USER_ID);
        List<Film> commonFilms = filmService.getCommonFilms(EXISTING_USER_ID, EXISTING_FRIEND_ID);
        assertThat(commonFilms).isEmpty();
    }
}