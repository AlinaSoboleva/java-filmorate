package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;

@Data
public class Review {
    private int reviewId;
    private String content;
    private boolean isPositive;
    private int userId;
    private int filmId;
    private int useful;
}
