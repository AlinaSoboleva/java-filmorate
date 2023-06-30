package ru.yandex.practicum.filmorate.storage;

public interface ReviewsLikeDao {

    void putLike(Integer reviewId, Integer userId);

    void deleteLike(Integer reviewId, Integer userId);

    void putDislike(Integer reviewId, Integer userId);

    void deleteDislike(Integer reviewId, Integer userId);
}
