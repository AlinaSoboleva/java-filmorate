package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;
import ru.yandex.practicum.filmorate.storage.ReviewsLikeDao;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.ReviewsDao;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.util.List;

@Service
public class ReviewsServiceImpl implements ReviewsService {

    private final ReviewsDao reviewsDao;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewsLikeDao reviewsLikeDao;

    public ReviewsServiceImpl(ReviewsDao reviewsDao, FilmDbStorage filmStorage, UserDbStorage userStorage, ReviewsLikeDao reviewsLikeDao) {
        this.reviewsDao = reviewsDao;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewsLikeDao = reviewsLikeDao;
    }

    @Override
    public void putLike(Integer reviewId, Integer userId) {
        userStorage.validationId(userId);
        reviewsDao.validationId(reviewId);
        reviewsLikeDao.putLike(reviewId, userId);
    }

    @Override
    public void putDislike(Integer reviewId, Integer userId) {
        userStorage.validationId(userId);
        reviewsDao.validationId(reviewId);
        reviewsLikeDao.putDislike(reviewId, userId);
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        userStorage.validationId(userId);
        reviewsDao.validationId(reviewId);
        reviewsLikeDao.deleteLike(reviewId, userId);
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        userStorage.validationId(userId);
        reviewsDao.validationId(reviewId);
        reviewsLikeDao.deleteDislike(reviewId, userId);
    }

    @Override
    public Review create(Review review) {
        filmStorage.validationId(review.getFilmId());
        userStorage.validationId(review.getUserId());
        return reviewsDao.create(review);
    }

    @Override
    public Review update(Review review) {
        filmStorage.validationId(review.getFilmId());
        userStorage.validationId(review.getUserId());
        reviewsDao.update(review);
        return reviewsDao.getById(review.getReviewId());
    }

    @Override
    public void delete(int id) {
        reviewsDao.delete(id);
    }

    @Override
    public Review getById(int id) {
        return reviewsDao.getById(id);
    }

    @Override
    public List<Review> findAll(int filmId, int count) {
        return reviewsDao.getReviews(filmId, count);
    }

}
