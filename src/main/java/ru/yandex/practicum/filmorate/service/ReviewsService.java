package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.film.Review;

import java.util.List;

public interface ReviewsService {
    Review create(Review review);

    Review update(Review review);

    void delete(int id);

    Review getById(int id);

    List<Review> findAll(int filmId, int count);

}
