package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.exceptions.DirectorIdException;
import ru.yandex.practicum.filmorate.model.film.Director;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorServiceImplTest extends BaseTest {

    private final DirectorService directorService;

    @Test
    void shouldReturnDirector1() {
        Director director = directorService.getDirectorById(EXISTING_DIRECTOR_ID);

        assertThat(director.getId()).isEqualTo(EXISTING_DIRECTOR_ID);
        assertThat(director.getName()).isEqualTo("director1");
    }

    @Test
    void shouldCreateDirectorWithId_3() {
        Director director = directorService.getDirectorById(EXISTING_DIRECTOR_ID);
        directorService.create(director);
        director = directorService.getDirectorById(3);

        assertThat(director.getId()).isEqualTo(3);
        assertThat(director.getName()).isEqualTo("director1");
    }

    @Test
    void shouldUpdateDirector() {
        Director director = directorService.getDirectorById(EXISTING_DIRECTOR_ID);
        director.setName("directorNew");
        directorService.update(director);
        director = directorService.getDirectorById(EXISTING_DIRECTOR_ID);

        assertThat(director.getId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(director.getName()).isEqualTo("directorNew");
    }

    @Test
    void shouldDeleteDirector() {
        directorService.delete(EXISTING_DIRECTOR_ID);

        assertThrows(
                DirectorIdException.class,
                () -> directorService.getDirectorById(EXISTING_DIRECTOR_ID)
        );
    }
}
