package ru.yandex.practicum.filmorate.storage;

public interface FilmLikeDao {

    void deleteLike(Integer filmId, Integer userId);

    void putLike(Integer filmId, Integer userId);
}
