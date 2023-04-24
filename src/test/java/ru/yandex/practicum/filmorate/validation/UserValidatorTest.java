package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidatorTest {
    private Validator validator;
    private User user;

    @BeforeEach
    void beforeEach() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @DisplayName("Проверяет валидацию корректного пользователя")
    @Test
    void checksTheValidUser() {
        user = new User("mail@mail.ru", "Alina", "Alina", LocalDate.of(2023, 4, 22));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
    }

    @DisplayName("Проверяет валидацию  пользователя, если email пустой")
    @Test
    void checksTheUserEmailIfItIsEmpty() {
        user = new User("", "Alina", "Alina", LocalDate.of(2023, 4, 22));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        String m = getMessage(violations);
        assertEquals("email не может быть пустым", m);
    }

    @DisplayName("Проверяет валидацию  пользователя, если email не содержит @")
    @Test
    void checksTheUserIfTheEmailDoesNotContainAt() {
        user = new User("mail", "Alina", "Alina", LocalDate.of(2023, 4, 22));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        String m = getMessage(violations);
        assertEquals("Некорректный email", m);
    }

    @DisplayName("Проверяет валидацию  пользователя, если логин пустой")
    @Test
    void checksTheUserLoginIfItIsEmpty() {
        user = new User("mail@mail.ru", "", "Alina", LocalDate.of(2023, 4, 22));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        String m = getMessage(violations);
        assertEquals("Логин не может быть пустым", m);
    }

    @DisplayName("Проверяет валидацию  пользователя, если логин содержит пробелы")
    @Test
    void checksTheUserLoginIfItIsContainsSpaces() {
        user = new User("mail@mail.ru", "Ali na", "Alina", LocalDate.of(2023, 4, 22));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        String m = getMessage(violations);
        assertEquals("Логин не может содержать пробелы", m);
    }

    @DisplayName("Проверяет валидацию  пользователя, если имя не указанно")
    @Test
    void checksTheUserLoginIfTheNameIsNull() {
        user = new User("mail@mail.ru", "Alina", null, LocalDate.of(2023, 4, 22));

        assertEquals("Alina", user.getName());
    }

    @DisplayName("Проверяет валидацию  пользователя, если дата рождения в будущем")
    @Test
    void checksTheUserIfTheBirthdayIsInTheFuture() {
        user = new User("mail@mail.ru", "Alina", "Alina", LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        String m = getMessage(violations);
        assertEquals("Дата рождения не может быть в будущем времени", m);
    }

    private String getMessage(Set<ConstraintViolation<User>> violations) {
        String m = "";
        Optional<String> message = violations.stream().findFirst().map(ConstraintViolation::getMessage);
        if (message.isPresent()) {
            m = message.get();
        }
        return m;
    }
}
