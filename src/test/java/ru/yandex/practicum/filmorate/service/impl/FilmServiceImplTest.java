package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.Exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.model.feed.EventOperation.*;
import static ru.yandex.practicum.filmorate.model.feed.EventType.*;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.EXISTING_FILM_ID;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.EXISTING_USER_ID;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.film.GenreDao;

import static ru.yandex.practicum.filmorate.testdata.TestConstants.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceImplTest extends BaseTest {

    private final FilmService filmService;
    private final EventFeedService eventFeedService;
    private final UserService userService;
    private final DirectorService directorService;
    private final MpaService mpaService;

    private final GenreDao genreDao;
    private final FilmLikeDao likeDao;

    @Test
    void whenAddLikeByNonExistingUser_getEventFeedForNonExistingUserThrows() {
        assertThrows(
                UserIdException.class,
                () -> filmService.putLike(EXISTING_FILM_ID, 1000)
        );
        assertThrows(
                UserIdException.class,
                () -> eventFeedService.getEventFeedForUser(1000)
        );
    }

    @Test
    void whenAddLikeToNonExistingFilm_eventNotSavedToDB() {
        assertThrows(
                FilmIdException.class,
                () -> filmService.putLike(1000, EXISTING_USER_ID)
        );
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenDeleteLikeOfNonExistingUser_getEventFeedForNonExistingUserThrows() {
        assertThrows(
                UserIdException.class,
                () -> filmService.deleteLike(EXISTING_FILM_ID, 1000)
        );
        assertThrows(
                UserIdException.class,
                () -> eventFeedService.getEventFeedForUser(1000)
        );
    }

    @Test
    void whenDeleteLikeOfNonExistingFilm_eventNotSavedToDB() {
        assertThrows(
                FilmIdException.class,
                () -> filmService.deleteLike(1000, EXISTING_USER_ID)
        );
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenAddLikeToFilm_eventSavedToDB() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID);

        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(1);
        Event event = eventFeedForUser.get(0);
        assertThat(event.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(event.getEntityId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(event.getEventType()).isEqualTo(LIKE);
        assertThat(event.getOperation()).isEqualTo(ADD);
    }

    @Test
    void whenAddLikeThenRemoveLike_twoEventsSavedToDB() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID);
        filmService.deleteLike(EXISTING_FILM_ID, EXISTING_USER_ID);

        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(2);
        Event addLikeEvent = eventFeedForUser.stream()
                .filter(e -> e.getOperation().equals(ADD))
                .collect(Collectors.toList())
                .get(0);

        Event removeLikeEvent = eventFeedForUser.stream()
                .filter(e -> e.getOperation().equals(REMOVE))
                .collect(Collectors.toList())
                .get(0);

        assertThat(addLikeEvent.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(addLikeEvent.getEntityId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(addLikeEvent.getEventType()).isEqualTo(LIKE);
        assertThat(addLikeEvent.getOperation()).isEqualTo(ADD);

        assertThat(removeLikeEvent.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(removeLikeEvent.getEntityId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(removeLikeEvent.getEventType()).isEqualTo(LIKE);
        assertThat(removeLikeEvent.getOperation()).isEqualTo(REMOVE);
    }

    @Test
    void whenDeleteFilmWithLikeGenre_filmIsDeleted() {

        Film byId = filmService.getById(EXISTING_FILM_ID);
        Genre genre = genreDao.getById(EXISTING_GENRE_ID);
        byId.getGenres().add(genre);
        likeDao.putLike(byId.getId(), EXISTING_USER_ID);

        filmService.update(byId);

        filmService.deleteFilm(EXISTING_FILM_ID);
        assertThrows(
                FilmIdException.class,
                () -> filmService.getById(EXISTING_FILM_ID)
        );
    }

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

    @Test
    void whenThereIsOnlyOneFilmWithTitle() {
        List<Film> films = filmService.search("OLD", "title");
        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0).getId()).isEqualTo(EXISTING_FILM_5_ID);
        assertThat(films.get(0).getName()).isEqualTo(EXISTING_FILM_5_NAME);
    }

    @Test
    void whenThereIsOnlyOneFilmWithDirector() {
        Film film = new Film("NewFilm", "Desc", LocalDate.of(2023, 6, 22), 180);
        Director director = directorService.getDirectorById(1);
        Mpa mpa = mpaService.getMpaById(1);
        List<Director> directors = new ArrayList<>();
        directors.add(0, director);
        film.setDirectors(directors);
        film.setMpa(mpa);
        filmService.create(film);
        List<Film> films = filmService.search("DIR", "director");
        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0).getDirectors().get(0).getName()).isEqualTo("director1");
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

    @Test
    void whenUpdateFilmWithDirectors_filmHasNewDirectors() {
        Film byId = filmService.getById(EXISTING_FILM_ID);
        byId.getDirectors().add(directorService.getDirectorById(EXISTING_DIRECTOR_ID));
        Film toUpdate = filmService.update(byId);
        List<Director> newDirectors = new ArrayList<>();
        newDirectors.add(directorService.getDirectorById(EXISTING_DIRECTOR_2_ID));
        toUpdate.setDirectors(newDirectors);
        Film updated = filmService.update(toUpdate);
        List<Director> directors = filmService.getById(updated.getId()).getDirectors();
        assertThat(directors.size()).isEqualTo(1);
        assertThat(directors.get(0).getId()).isEqualTo(EXISTING_DIRECTOR_2_ID);
    }
}