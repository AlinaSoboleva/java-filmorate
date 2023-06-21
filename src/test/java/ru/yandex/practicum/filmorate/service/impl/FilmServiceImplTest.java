package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.LikeDao;
import ru.yandex.practicum.filmorate.storage.film.GenreDao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceImplTest extends BaseTest {

    private final FilmServiceImpl filmService;
    private final GenreDao genreDao;
    private final LikeDao likeDao;

    @Test
    void whenDeleteFilmWithLikeGenre_filmIsDeleted() {

        Film byId = filmService.getById(EXISTING_FILM_ID);
        Genre genre = genreDao.getById(EXISTING_GENRE_ID);
        byId.getGenres().add(genre);
        likeDao.putLike(byId.getId(), EXISTING_USER_ID);

        filmService.update(byId);

        filmService.deleteFilm(EXISTING_FILM_ID);
        assertThrows(
                FilmIdException.class,
                () -> filmService.getById(EXISTING_FILM_ID)
        );
    }

}