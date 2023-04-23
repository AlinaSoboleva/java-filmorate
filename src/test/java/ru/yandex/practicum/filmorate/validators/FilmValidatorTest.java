package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    private FilmValidator filmValidator;
    private Film film;

    @BeforeEach
    void beforeEach() {
        this.filmValidator = new FilmValidator();
    }

    @DisplayName("Проверяет валидацию корректного фильма")
    @Test
    void checksTheValidFilm() {
        film = new Film("Титаник", "описание", LocalDate.of(1895, 12, 28), 100);
        boolean isValid = filmValidator.isValidFilm(film);

        assertTrue(isValid);
    }

    @DisplayName("Проверяет валидацию фильма, если имя пустое")
    @Test
    void checksTheFilmNameIfItIsEmpty() {
        film = new Film("", "описание", LocalDate.of(2010, 01, 01), 100);
        final InvalidFilmException ex = assertThrows(InvalidFilmException.class, () ->
                filmValidator.isValidFilm(film));

        assertEquals("Название фильма не может быть пустым", ex.getMessage());
    }

    @DisplayName("Проверяет валидацию фильма, если имя содержит только пробелы")
    @Test
    void checksTheFilmNameIfItIsContainsOnlySpaces() {
        film = new Film("  ", "описание", LocalDate.of(2010, 01, 01), 100);
        final InvalidFilmException ex = assertThrows(InvalidFilmException.class, () ->
                filmValidator.isValidFilm(film));

        assertEquals("Название фильма не может быть пустым", ex.getMessage());
    }

    @DisplayName("Проверяет валидацию фильма, если описание содержит 201 символ")
    @Test
    void checksTheFilmDescriptionContaining201Characters() {
        film = new Film("Титаник", "123456789_123456789_123456789" +
                "_123456789_123456789_123456789_123456789_123456789_123456789_123456789" +
                "_123456789_123456789_123456789_123456789_123456789_123456789_123456789" +
                "_123456789_123456789_123456789_1",
                LocalDate.of(2010, 01, 01), 100);
        final InvalidFilmException ex = assertThrows(InvalidFilmException.class, () ->
                filmValidator.isValidFilm(film));

        assertEquals("Описание фильма слишком длинное", ex.getMessage());
    }

    @DisplayName("Проверяет валидацию фильма, если его дата релиза 27 декабря 1895")
    @Test
    void checksTheFilmIfTheReleaseDateIsDecember27Year1895() {
        film = new Film("Титаник", "описание", LocalDate.of(1895, 12, 27), 100);
        final InvalidFilmException ex = assertThrows(InvalidFilmException.class, () ->
                filmValidator.isValidFilm(film));

        assertEquals("Дата релиза  не может быть  раньше 28 декабря 1895 года", ex.getMessage());
    }

    @DisplayName("Проверяет валидацию фильма, если его продолжительность -1")
    @Test
    void checksTheFilmIfItsDurationIsNegative() {
        film = new Film("Титаник", "описание", LocalDate.of(1895, 12, 29), -1);
        final InvalidFilmException ex = assertThrows(InvalidFilmException.class, () ->
                filmValidator.isValidFilm(film));

        assertEquals("Продолжительность фильма не может быть отрицательной", ex.getMessage());
    }
}