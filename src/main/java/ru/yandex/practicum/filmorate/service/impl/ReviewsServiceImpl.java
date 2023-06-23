package ru.yandex.practicum.filmorate.service.impl;

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
public class ReviewsServiceImpl implements ReviewsService {

    private final ReviewsDao reviewsDao;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewsLikeDao reviewsLikeDao;
    private final EventFeedService eventFeedService;

    public ReviewsServiceImpl(ReviewsDao reviewsDao,
                              @Qualifier("filmDbStorage") FilmStorage filmStorage,
                              @Qualifier("userDbStorage") UserStorage userStorage,
                              ReviewsLikeDao reviewsLikeDao,
                              EventFeedService eventFeedService) {
        this.reviewsDao = reviewsDao;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewsLikeDao = reviewsLikeDao;
        this.eventFeedService = eventFeedService;
    }

    @Override
    public void putLike(Integer reviewId, Integer userId) {
        userStorage.validationId(userId);
        reviewsDao.validationId(reviewId);
        reviewsLikeDao.putLike(reviewId, userId);
        eventFeedService.saveEvent(EventType.LIKE, EventOperation.ADD, userId, reviewId);
    }

    @Override
    public void putDislike(Integer reviewId, Integer userId) {
        userStorage.validationId(userId);
        reviewsDao.validationId(reviewId);
        reviewsLikeDao.putDislike(reviewId, userId);
        eventFeedService.saveEvent(EventType.LIKE, EventOperation.ADD, userId, reviewId);
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        userStorage.validationId(userId);
        reviewsDao.validationId(reviewId);
        reviewsLikeDao.deleteLike(reviewId, userId);
        eventFeedService.saveEvent(EventType.LIKE, EventOperation.REMOVE, userId, reviewId);
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        userStorage.validationId(userId);
        reviewsDao.validationId(reviewId);
        reviewsLikeDao.deleteDislike(reviewId, userId);
        eventFeedService.saveEvent(EventType.LIKE, EventOperation.REMOVE, userId, reviewId);
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
        eventFeedService.saveEvent(EventType.REVIEW, EventOperation.UPDATE, review.getUserId(), review.getReviewId());
        return reviewsDao.getById(review.getReviewId());
    }

    @Override
    public void delete(int id) {
        Integer userId = reviewsDao.deleteAndGetUserId(id);
        if (deleteSuccessful(userId)) {
            eventFeedService.saveEvent(EventType.REVIEW, EventOperation.REMOVE, userId, id);
        }
    }

    private boolean deleteSuccessful(Integer userId) {
        return null != userId && userId != 0;
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
