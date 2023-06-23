package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Review;

import java.util.List;

public interface ReviewsDao {
    List<Review> getReviews(int filmId, int count);

    Review create(Review review);

    boolean update(Review review);

    Review getById(int id);

    void validationId(Integer id);

    Integer deleteAndReturnUserId(Integer id);
}
