package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;


@Data
public class Film {
    private int id = 0;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
}
