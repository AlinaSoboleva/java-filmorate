package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;

@Data
public class Review {
    int reviewId;
    String content;
    boolean isPositive;
    int userId;
    int filmId;
    int useful;
}
