package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.Exceptions.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidator {

    public boolean isValidUser(User user){
        if (isValidEmail(user)){
            throw new InvalidUserException("Некорректный email");
        }
        if (isValidLogin(user)){
            createName(user);
        }else {
            throw new InvalidUserException("Некорректный логин");
        }
        if (isValidBirthday(user)){
            throw new InvalidUserException("Дата рождения не может быть в будущем");
        }
        return true;
    }

    private boolean isValidLogin(User user){
        user.setLogin(user.getLogin().trim());
        return !user.getLogin().isBlank() && !user.getLogin().contains(" ");
    }

    private boolean isValidEmail(User user){
        user.setEmail(user.getEmail().trim());
        return user.getEmail().isBlank() || !user.getEmail().contains("@");
    }

    private void createName(User user){
        if (user.getName()==null){
            user.setName(user.getLogin());
        }
    }

    private boolean isValidBirthday(User user){
        return !user.getBirthday().isBefore(LocalDate.now());
    }
}
