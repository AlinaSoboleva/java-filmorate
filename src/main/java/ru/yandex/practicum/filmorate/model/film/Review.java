package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;

@Data
public class Review {
    private  int  reviewId;
    private final String content;
    private final boolean isPositive;
    private final int userId;
    private final int filmId;
    private final int useful;
}
