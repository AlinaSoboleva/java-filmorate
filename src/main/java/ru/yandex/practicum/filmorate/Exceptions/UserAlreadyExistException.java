package ru.yandex.practicum.filmorate.Exceptions;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(){}
    public UserAlreadyExistException(String m){
        super(m);
    }
}
