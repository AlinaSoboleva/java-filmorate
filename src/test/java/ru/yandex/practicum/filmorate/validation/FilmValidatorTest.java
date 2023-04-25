package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    private Validator validator;
    private Film film;

    @BeforeEach
    void beforeEach() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @DisplayName("Проверяет валидацию корректного фильма")
    @Test
    void checksTheValidFilm() {
        film = new Film("Титаник", "описание", LocalDate.of(1895, 12, 28), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(violations.size(), 0);
    }

    @DisplayName("Проверяет валидацию фильма с продолжительностью -1")
    @Test
    void checkAFilmWithANegativeDuration() {
        Film film = new Film("Титаник", "описание", LocalDate.of(1895, 12, 29), -1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        String m = getMessage(violations);
        assertEquals("Продолжительность фильма не может быть отрицательной", m);
    }

    @DisplayName("Проверяет валидацию фильма, если имя пустое")
    @Test
    void checksTheFilmNameIfItIsEmpty() {
        film = new Film("", "описание", LocalDate.of(2010, 1, 1), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        String m = getMessage(violations);
        assertEquals("Название фильма не может быть пустым", m);
    }

    @DisplayName("Проверяет валидацию фильма, если имя содержит только пробелы")
    @Test
    void checksTheFilmNameIfItIsContainsOnlySpaces() {
        film = new Film("  ", "описание", LocalDate.of(2010, 1, 1), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        String m = getMessage(violations);
        assertEquals("Название фильма не может быть пустым", m);
    }

    @DisplayName("Проверяет валидацию фильма, если описание содержит 201 символ")
    @Test
    void checksTheFilmDescriptionContaining201Characters() {
        film = new Film("Титаник", "123456789_123456789_123456789" +
                "_123456789_123456789_123456789_123456789_123456789_123456789_123456789" +
                "_123456789_123456789_123456789_123456789_123456789_123456789_123456789" +
                "_123456789_123456789_123456789_1",
                LocalDate.of(2010, 1, 1), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        String m = getMessage(violations);
        assertEquals("Описание фильма превышает 200 символов", m);
    }

    @DisplayName("Проверяет валидацию фильма, если его дата релиза 27 декабря 1895")
    @Test
    void checksTheFilmIfTheReleaseDateIsDecember27Year1895() {
        film = new Film("Титаник", "описание", LocalDate.of(1895, 12, 27), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        String m = getMessage(violations);
        assertEquals("Дата релиза должна быть позднее чем 28 декабря 1895", m);
    }

    private String getMessage(Set<ConstraintViolation<Film>> violations) {
        String m = "";
        Optional<String> message = violations.stream().findFirst().map(ConstraintViolation::getMessage);
        if (message.isPresent()) {
            m = message.get();
        }
        return m;
    }
}