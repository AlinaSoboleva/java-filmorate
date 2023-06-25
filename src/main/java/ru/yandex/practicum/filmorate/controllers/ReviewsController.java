package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewsController {

    private final ReviewsService reviewsService;

    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewsService.putLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewsService.putDislike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Integer id) {
        reviewsService.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewsService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewsService.deleteDislike(id, userId);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(defaultValue = "0") int filmId, @RequestParam(defaultValue = "10") int count) {
        return reviewsService.findAll(filmId, count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getById(@PathVariable Integer id) {
        return new ResponseEntity<>(reviewsService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody Review review) {
        if (review.getUserId() == 0 || review.getFilmId() == 0) {
            return new ResponseEntity<>(review, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(reviewsService.create(review), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Review> update(@Valid @RequestBody Review review) {
        Review updatedReview = reviewsService.update(review);
        if (updatedReview == null) {
            return new ResponseEntity<>(review, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }
}
