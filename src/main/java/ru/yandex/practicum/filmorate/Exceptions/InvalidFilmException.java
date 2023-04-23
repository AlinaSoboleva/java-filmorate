package ru.yandex.practicum.filmorate.Exceptions;

public class InvalidFilmException extends RuntimeException{
    public  InvalidFilmException(){}
    public InvalidFilmException(String m){
        super(m);
    }
}
