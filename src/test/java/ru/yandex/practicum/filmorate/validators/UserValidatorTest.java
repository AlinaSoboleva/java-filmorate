package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exceptions.InvalidFilmException;
import ru.yandex.practicum.filmorate.Exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private  UserValidator userValidator;
    private User user;

    @BeforeEach
    void beforeEach(){
        this.userValidator = new UserValidator();
    }

    @DisplayName("Проверяет валидацию корректного пользователя")
    @Test
    void checksTheValidUser() {
        user = new User("mail@mail.ru","Alina","Alina", LocalDate.of(2023,4,22));

        assertTrue(userValidator.isValidUser(user));
    }

    @DisplayName("Проверяет валидацию  пользователя, если email пустой")
    @Test
    void checksTheUserEmailIfItIsEmpty() {
        user = new User("","Alina","Alina", LocalDate.of(2023,4,22));
        final InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                userValidator.isValidUser(user));

        assertEquals("Некорректный email",ex.getMessage());
    }

    @DisplayName("Проверяет валидацию  пользователя, если email не содержит @")
    @Test
    void checksTheUserIfTheEmailDoesNotContainAt() {
        user = new User("mail","Alina","Alina", LocalDate.of(2023,4,22));
        final InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                userValidator.isValidUser(user));

        assertEquals("Некорректный email",ex.getMessage());
    }

    @DisplayName("Проверяет валидацию  пользователя, если логин пустой")
    @Test
    void checksTheUserLoginIfItIsEmpty() {
        user = new User("mail@mail.ru","","Alina", LocalDate.of(2023,4,22));
        final InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                userValidator.isValidUser(user));

        assertEquals("Некорректный логин",ex.getMessage());
    }

    @DisplayName("Проверяет валидацию  пользователя, если логин содержит пробелы")
    @Test
    void checksTheUserLoginIfItIsContainsSpaces() {
        user = new User("mail@mail.ru","Ali na","Alina", LocalDate.of(2023,4,22));
        final InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                userValidator.isValidUser(user));

        assertEquals("Некорректный логин",ex.getMessage());
    }

    @DisplayName("Проверяет валидацию  пользователя, если имя не указанно")
    @Test
    void checksTheUserLoginIfTheNameIsNull() {
        user = new User("mail@mail.ru","Alina",null, LocalDate.of(2023,4,22));
        boolean isValid = userValidator.isValidUser(user);

        assertTrue(isValid);
        assertEquals("Alina",user.getName());
    }

    @DisplayName("Проверяет валидацию  пользователя, если дата рождения в будущем")
    @Test
    void checksTheUserIfTheBirthdayIsInTheFuture() {
        user = new User("mail@mail.ru","Alina","Alina", LocalDate.now().plusDays(1));
        final InvalidUserException ex = assertThrows(InvalidUserException.class, () ->
                userValidator.isValidUser(user));

        assertEquals("Дата рождения не может быть в будущем",ex.getMessage());
    }
}