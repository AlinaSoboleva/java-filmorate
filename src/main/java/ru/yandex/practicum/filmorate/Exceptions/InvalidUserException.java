package ru.yandex.practicum.filmorate.Exceptions;

public class InvalidUserException extends RuntimeException{
    public InvalidUserException (String m){
        super(m);
    }
}
