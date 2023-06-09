package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Genre {
    private final int id;
    @EqualsAndHashCode.Exclude
    private final String name;

}
