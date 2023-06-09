package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {

    private int id = 0;

    @Email(message = "Некорректный email")
    @NotEmpty(message = "email не может быть пустым")
    @NonNull
    private String email;
    @NonNull
    @NotEmpty(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @NonNull
    @Past(message = "Дата рождения не может быть в будущем времени")
    private LocalDate birthday;

    public User(@NonNull String email, @NonNull String login, String name, @NonNull LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = setName(name);
        this.birthday = birthday;
    }

    private String setName(String name) {
        if (name == null || name.isBlank()) {
            return login;
        }
        return name;
    }
}

