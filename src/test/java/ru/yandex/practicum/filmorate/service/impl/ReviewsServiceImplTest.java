package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.Exceptions.ReviewsIdException;
import ru.yandex.practicum.filmorate.Exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.model.feed.EventOperation.*;
import static ru.yandex.practicum.filmorate.model.feed.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewsServiceImplTest extends BaseTest {

    private final ReviewsService reviewsService;
    private final EventFeedService eventFeedService;

    @Test
    void whenDeleteReview_eventSavedToDB() {
        Review review = new Review("Content", true, EXISTING_FRIEND_ID, EXISTING_FILM_ID, 0);
        Review created = reviewsService.create(review);
        reviewsService.delete(created.getReviewId());

        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_FRIEND_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(2);
        Event firstEvent = eventFeedForUser.get(0);
        assertThat(firstEvent.getEventType()).isEqualTo(REVIEW);
        assertThat(firstEvent.getOperation()).isEqualTo(ADD);
        assertThat(firstEvent.getUserId()).isEqualTo(EXISTING_FRIEND_ID);

        Event secondEvent = eventFeedForUser.get(1);
        assertThat(secondEvent.getEventType()).isEqualTo(REVIEW);
        assertThat(secondEvent.getOperation()).isEqualTo(REMOVE);
        assertThat(secondEvent.getUserId()).isEqualTo(EXISTING_FRIEND_ID);
    }

    @Test
    void whenUpdateReview_eventSavedToDB() {
        Review review = new Review("Content", true, EXISTING_FRIEND_ID, EXISTING_FILM_ID, 0);
        Review created = reviewsService.create(review);
        Review updated = new Review("New Content", false, EXISTING_FRIEND_ID, EXISTING_FILM_ID, 0);
        updated.setReviewId(created.getReviewId());
        reviewsService.update(updated);

        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_FRIEND_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(2);
        Event firstEvent = eventFeedForUser.get(0);
        assertThat(firstEvent.getEventType()).isEqualTo(REVIEW);
        assertThat(firstEvent.getOperation()).isEqualTo(ADD);
        assertThat(firstEvent.getUserId()).isEqualTo(EXISTING_FRIEND_ID);

        Event secondEvent = eventFeedForUser.get(1);
        assertThat(secondEvent.getEventType()).isEqualTo(REVIEW);
        assertThat(secondEvent.getOperation()).isEqualTo(UPDATE);
        assertThat(secondEvent.getUserId()).isEqualTo(EXISTING_FRIEND_ID);
    }

    @Test
    void whenCreateReview_eventSavedToDB() {
        Review review = new Review("Content", true, EXISTING_FRIEND_ID, EXISTING_FILM_ID, 0);
        reviewsService.create(review);
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_FRIEND_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(1);
        Event event = eventFeedForUser.get(0);
        assertThat(event.getEventType()).isEqualTo(REVIEW);
        assertThat(event.getOperation()).isEqualTo(ADD);
        assertThat(event.getUserId()).isEqualTo(EXISTING_FRIEND_ID);
    }

    @Test
    void putADoubleLikeToOneReviewFromOneUser() {
        reviewsService.putLike(EXISTING_REVIEW_ID, EXISTING_USER_ID);
        reviewsService.putLike(EXISTING_REVIEW_ID, EXISTING_USER_ID);

        assertThat(reviewsService.getById(EXISTING_REVIEW_ID)).hasFieldOrPropertyWithValue("useful", 1);
    }

    @Test
    void putADoubleDislikeToOneReviewFromOneUser() {
        reviewsService.putDislike(EXISTING_REVIEW_ID, EXISTING_USER_ID);
        reviewsService.putDislike(EXISTING_REVIEW_ID, EXISTING_USER_ID);

        assertThat(reviewsService.getById(EXISTING_REVIEW_ID)).hasFieldOrPropertyWithValue("useful", -1);
    }

    @Test
    void putOneLikeAndDeleteLike() {
        reviewsService.putLike(EXISTING_REVIEW_ID, EXISTING_USER_ID);

        assertThat(reviewsService.getById(EXISTING_REVIEW_ID)).hasFieldOrPropertyWithValue("useful", 1);

        reviewsService.deleteLike(EXISTING_REVIEW_ID, EXISTING_USER_ID);

        assertThat(reviewsService.getById(EXISTING_REVIEW_ID)).hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    void putOneDislikeAndDeleteDislike() {
        reviewsService.putDislike(EXISTING_REVIEW_ID, EXISTING_USER_ID);

        assertThat(reviewsService.getById(EXISTING_REVIEW_ID)).hasFieldOrPropertyWithValue("useful", -1);

        reviewsService.deleteDislike(EXISTING_REVIEW_ID, EXISTING_USER_ID);

        assertThat(reviewsService.getById(EXISTING_REVIEW_ID)).hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    void putAndDeleteLikeAndDislikeWitchANotExistsUserId() {
        assertThrows(UserIdException.class, () -> reviewsService.putLike(EXISTING_REVIEW_ID, 3));

        assertThrows(UserIdException.class, () -> reviewsService.putDislike(EXISTING_REVIEW_ID, 3));

        assertThrows(UserIdException.class, () -> reviewsService.deleteLike(EXISTING_REVIEW_ID, 3));

        assertThrows(UserIdException.class, () -> reviewsService.deleteDislike(EXISTING_REVIEW_ID, 3));
    }

    @Test
    void putAndDeleteLikeAndDislikeWitchANotExistsReviewId() {
        assertThrows(ReviewsIdException.class, () -> reviewsService.putLike(3, EXISTING_USER_ID));

        assertThrows(ReviewsIdException.class, () -> reviewsService.putDislike(3, EXISTING_USER_ID));

        assertThrows(ReviewsIdException.class, () -> reviewsService.deleteLike(3, EXISTING_USER_ID));

        assertThrows(ReviewsIdException.class, () -> reviewsService.deleteDislike(3, EXISTING_USER_ID));
    }

    @Test
    void createAndDeleteReview() {
        Review review = new Review("Bad comment", false, 2, 1, 0);
        reviewsService.create(review);

        List<Review> reviews = reviewsService.findAll(1, 10);

        assertThat(reviews.size()).isEqualTo(2);
        assertThat(reviews.get(1)).hasFieldOrPropertyWithValue("content", "Bad comment");

        reviewsService.delete(review.getReviewId());

        List<Review> reviewsDelete = reviewsService.findAll(1, 10);

        assertThat(reviewsDelete.size()).isEqualTo(1);
    }

    @Test
    void createWitchANotExistentUserId() {
        Review review = new Review("Bad comment", false, 4, 1, 0);
        assertThrows(UserIdException.class, () -> reviewsService.create(review));
    }

    @Test
    void createWitchANotExistentFilmId() {
        Review review = new Review("Bad comment", false, 1, 1000, 0);
        assertThrows(FilmIdException.class, () -> reviewsService.create(review));
    }

    @Test
    void getReviewById() {
        Review review = reviewsService.getById(EXISTING_REVIEW_ID);

        assertThat(review).hasFieldOrPropertyWithValue("content", "good comment");
    }

    @Test
    void getReviewByWithANonExistentId() {
        reviewsService.delete(EXISTING_REVIEW_ID);
        assertThrows(ReviewsIdException.class, () -> reviewsService.getById(EXISTING_REVIEW_ID));
    }
}
