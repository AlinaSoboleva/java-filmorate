package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.ReviewsService;
import ru.yandex.practicum.filmorate.storage.ReviewsLikeDao;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.ReviewsDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewsServiceImpl implements ReviewsService {

    private final ReviewsDao reviewsDao;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;
    private final ReviewsLikeDao reviewsLikeDao;
    private final EventFeedService eventFeedService;

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
        Review created = reviewsDao.create(review);
        eventFeedService.saveEvent(EventType.REVIEW, EventOperation.ADD, created.getUserId(), created.getReviewId());
        return created;
    }

    @Override
    public Review update(Review review) {
        filmStorage.validationId(review.getFilmId());
        userStorage.validationId(review.getUserId());
        reviewsDao.update(review);
        Review updated = reviewsDao.getById(review.getReviewId());
        if (updated != null) {
            eventFeedService.saveEvent(EventType.REVIEW, EventOperation.UPDATE, updated.getUserId(), review.getReviewId());
        }
        return updated;
    }

    @Override
    public void delete(int id) {
        Integer userId = reviewsDao.deleteAndReturnUserId(id);
        if (userId != null && userId != 0) {
            eventFeedService.saveEvent(EventType.REVIEW, EventOperation.REMOVE, userId, id);
        }
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