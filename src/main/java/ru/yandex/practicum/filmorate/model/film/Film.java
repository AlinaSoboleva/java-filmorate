package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.CheckReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {

    private int id = 0;
    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Описание фильма превышает 200 символов")
    private final String description;
    @CheckReleaseDate
    private final LocalDate releaseDate;
    @PositiveOrZero(message = "Продолжительность фильма не может быть отрицательной")
    private final int duration;

    private Mpa mpa;

    private double rate;

    private final Set<Genre> genres = new HashSet<>();

    private List<Director> directors = new ArrayList<>();
}
