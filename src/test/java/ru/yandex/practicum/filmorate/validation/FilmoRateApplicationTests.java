package ru.yandex.practicum.filmorate.validation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.MpaStorage;
import ru.yandex.practicum.filmorate.storage.impl.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests extends BaseTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final MpaStorage mpaStorage;

    private final GenreStorage genreStorage;
    private final FilmLikeStorage likeStorage;


    @Test
    @DisplayName("Получение пользователя по id")
    public void testFindUserById() {

        User user = userStorage.getById(1);

        assertThat(user).hasFieldOrPropertyWithValue("id", 1);
        assertThat(user).hasFieldOrPropertyWithValue("email", "em@mail.ru");
    }

    @Test
    @DisplayName("Получение всех пользователей")
    public void testFindAllUsers() {

        List<User> users = (List<User>) userStorage.getUsers();

        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Сохранине пользователя в базе данных c последующим удалением")
    public void testCreateAndDeleteUser() {
        User user = new User("email@yandex.ru", "user3", "name", LocalDate.of(1900, 2, 1));
        userStorage.create(user);
        List<User> users = (List<User>) userStorage.getUsers();

        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(2)).hasFieldOrPropertyWithValue("email", "email@yandex.ru");

        userStorage.delete(user.getId());

        List<User> users2 = (List<User>) userStorage.getUsers();

        assertThat(users2.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    public void testUpdateUser() {
        User user = new User("newemail@yandex.ru", "newUser", "", LocalDate.of(1901, 2, 1));
        user.setId(1);
        userStorage.update(user);
        List<User> users = (List<User>) userStorage.getUsers();

        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("email", "newemail@yandex.ru");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("login", "newUser");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("name", "newUser");
    }

    @Test
    @DisplayName("Получение фильма по id")
    public void testFindFilmById() {

        Film film = filmStorage.getById(1);

        assertThat(film).hasFieldOrPropertyWithValue("id", 1);
        assertThat(film).hasFieldOrPropertyWithValue("name", "FILM");
    }

    @Test
    @DisplayName("Получение всех фильмов")
    public void testFindAllFilms() {

        List<Film> films = (List<Film>) filmStorage.getFilms();

        assertThat(films.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("Сохранине фильма в базе данных c последующим удалением")
    public void testCreateAndDeleteFilm() {
        Film film = new Film("newFilm", "des", LocalDate.of(2002, 3, 4), 100);
        film.setMpa(new Mpa(1,"G"));
        filmStorage.create(film);
        List<Film> films = (List<Film>) filmStorage.getFilms();

        assertThat(films.size()).isEqualTo(6);
        assertThat(films.get(5)).hasFieldOrPropertyWithValue("name", "newFilm");

        filmStorage.delete(film.getId());

        List<Film> films2 = (List<Film>) filmStorage.getFilms();

        assertThat(films2.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("Обновление данных фильма")
    public void testUpdateFilm() {
        Film film = new Film("newFilm", "newDesc", LocalDate.of(2002, 3, 4), 100);
        film.setId(1);
        film.setMpa(new Mpa(1,"G"));
        filmStorage.update(film);
        List<Film> films = (List<Film>) filmStorage.getFilms();

        assertThat(films.size()).isEqualTo(5);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("name", "newFilm");
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("description", "newDesc");
    }

    @Test
    @DisplayName("Получение рейтинга по id")
    public void testFindMpaById() {
        Mpa mpa = mpaStorage.getById(1);

        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    @DisplayName("Получение списка рейтингов")
    public void testFindAllMpa() {
        List<Mpa> mpaList = (List<Mpa>) mpaStorage.findAll();

        assertThat(mpaList.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("Получение жанра по id")
    public void testFindGenreById() {
        Genre genre = genreStorage.getById(1);

        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    @DisplayName("Получение списка всех жанров")
    public void testFindAllGenres() {
        List<Genre> genreList = (List<Genre>) genreStorage.findAll();

        assertThat(genreList.size()).isEqualTo(6);
    }

    @Test
    @DisplayName("Получение рекомендаций фильмов по id")
    public void testGetRecommendations() {
        likeStorage.putLike(2,1, 10);
        likeStorage.putLike(3,1, 10);
        likeStorage.putLike(1,2, 10);//фильм для рекомендации - FILM
        likeStorage.putLike(2,2, 10);
        likeStorage.putLike(3,2, 10);

        List<Film> films = filmStorage.getRecommendations(1);

        assertThat(films.size()).isEqualTo(1);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("name", "FILM");
    }
}