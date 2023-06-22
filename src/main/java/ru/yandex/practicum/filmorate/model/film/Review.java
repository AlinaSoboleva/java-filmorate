package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    private  int  reviewId = 0;
    @NotNull
    @NotBlank
    private final String content;

    @NotNull
    private final Boolean isPositive;

    @NotNull(message = "ID пользователя не может быть пустым")
    private final int userId;

    @NotNull(message = "ID фильма не может быть пустым")
    private final int filmId;
    private final int useful;

    public boolean getIsPositive() {
        return isPositive;
    }
}
