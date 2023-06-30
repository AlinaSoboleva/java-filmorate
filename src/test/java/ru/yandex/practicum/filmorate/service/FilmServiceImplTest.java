package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.exceptions.DirectorIdException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.SearchBy;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceImplTest extends BaseTest {

    private final FilmService filmService;
    private final DirectorStorage directorStorage;
    private final UserService userService;
    private final GenreService genreService;

    @Test
    void findTopFilmsByGenreYear_returnsFilms_sortedByRate() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 10);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 9); // total 9.5

        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 8); // total 8.5

        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_USER_ID, 8);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID, 7); // total 7.5

        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_USER_ID, 7);
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_FRIEND_ID, 6); // total 6.5

        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_USER_ID, 6);
        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_FRIEND_ID, 5); // total 5.5

        updateFilmSetGenre(EXISTING_FILM_ID, EXISTING_GENRE_ID);
        updateFilmSetGenre(EXISTING_FILM_2_ID, EXISTING_GENRE_ID);
        updateFilmSetGenre(EXISTING_FILM_3_ID, EXISTING_GENRE_ID);
        updateFilmSetGenre(EXISTING_FILM_4_ID, EXISTING_GENRE_ID);
        updateFilmSetGenre(EXISTING_FILM_5_ID, EXISTING_GENRE_ID);


        List<Film> result = new ArrayList<>(filmService.findAllTopFilms(10, EXISTING_GENRE_ID, 2022));
        assertThat(result.size()).isEqualTo(5);

        assertThat(result.get(0).getRate()).isEqualTo(9.5);
        assertThat(result.get(1).getRate()).isEqualTo(8.5);
        assertThat(result.get(2).getRate()).isEqualTo(7.5);
        assertThat(result.get(3).getRate()).isEqualTo(6.5);
        assertThat(result.get(4).getRate()).isEqualTo(5.5);
    }

    @Test
    void findTopFilmsByGenre_returnsFilms_sortedByRate() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 10);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 9); // total 9.5

        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 8); // total 8.5

        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_USER_ID, 8);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID, 7); // total 7.5

        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_USER_ID, 7);
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_FRIEND_ID, 6); // total 6.5

        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_USER_ID, 6);
        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_FRIEND_ID, 5); // total 5.5

        updateFilmSetGenre(EXISTING_FILM_ID, EXISTING_GENRE_ID);
        updateFilmSetGenre(EXISTING_FILM_2_ID, EXISTING_GENRE_ID);
        updateFilmSetGenre(EXISTING_FILM_3_ID, EXISTING_GENRE_ID);
        updateFilmSetGenre(EXISTING_FILM_4_ID, EXISTING_GENRE_ID);
        updateFilmSetGenre(EXISTING_FILM_5_ID, EXISTING_GENRE_ID);


        List<Film> result = new ArrayList<>(filmService.findAllTopFilms(10, EXISTING_GENRE_ID, null));
        assertThat(result.size()).isEqualTo(5);

        assertThat(result.get(0).getRate()).isEqualTo(9.5);
        assertThat(result.get(1).getRate()).isEqualTo(8.5);
        assertThat(result.get(2).getRate()).isEqualTo(7.5);
        assertThat(result.get(3).getRate()).isEqualTo(6.5);
        assertThat(result.get(4).getRate()).isEqualTo(5.5);
    }

    private void updateFilmSetGenre(Integer filmId, Integer genreId) {
        Film first = filmService.getById(filmId);
        first.getGenres().add(genreService.getGenreById(genreId));
        filmService.update(first);
    }

    @Test
    void findTopFilmsByYear_returnsFilms_sortedByRate() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 10);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 9); // total 9.5

        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 8); // total 8.5

        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_USER_ID, 8);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID, 7); // total 7.5

        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_USER_ID, 7);
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_FRIEND_ID, 6); // total 6.5

        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_USER_ID, 6);
        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_FRIEND_ID, 5); // total 5.5

        List<Film> result = new ArrayList<>(filmService.findAllTopFilms(10, null, 2022));
        assertThat(result.size()).isEqualTo(5);

        assertThat(result.get(0).getRate()).isEqualTo(9.5);
        assertThat(result.get(1).getRate()).isEqualTo(8.5);
        assertThat(result.get(2).getRate()).isEqualTo(7.5);
        assertThat(result.get(3).getRate()).isEqualTo(6.5);
        assertThat(result.get(4).getRate()).isEqualTo(5.5);
    }

    @Test
    void findTopFilms_returnsFilms_sortedByRate() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 10);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 9); // total 9.5

        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 8); // total 8.5

        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_USER_ID, 8);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID, 7); // total 7.5

        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_USER_ID, 7);
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_FRIEND_ID, 6); // total 6.5

        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_USER_ID, 6);
        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_FRIEND_ID, 5); // total 5.5

        List<Film> result = new ArrayList<>(filmService.findAllTopFilms(10, null, null));
        assertThat(result.size()).isEqualTo(5);

        assertThat(result.get(0).getRate()).isEqualTo(9.5);
        assertThat(result.get(1).getRate()).isEqualTo(8.5);
        assertThat(result.get(2).getRate()).isEqualTo(7.5);
        assertThat(result.get(3).getRate()).isEqualTo(6.5);
        assertThat(result.get(4).getRate()).isEqualTo(5.5);
    }

    @Test
    void getCommonFilms_returnsFilms_sortedByRate() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 10);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 9); // total 9.5

        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 8); // total 8.5

        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_USER_ID, 8);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID, 7); // total 7.5

        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_USER_ID, 7);
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_FRIEND_ID, 6); // total 6.5

        List<Film> result = filmService.getCommonFilms(EXISTING_USER_ID, EXISTING_FRIEND_ID);
        assertThat(result.size()).isEqualTo(4);

        assertThat(result.get(0).getRate()).isEqualTo(9.5);
        assertThat(result.get(1).getRate()).isEqualTo(8.5);
        assertThat(result.get(2).getRate()).isEqualTo(7.5);
        assertThat(result.get(3).getRate()).isEqualTo(6.5);
    }

    @Test
    void search_returnsFilms_sortedByRate() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 10);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 9); // total 9.5

        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 8); // total 8.5

        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_USER_ID, 8);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID, 7); // total 7.5

        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_USER_ID, 7);
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_FRIEND_ID, 6); // total 6.5
        List<Film> result = filmService.search("FI", List.of(SearchBy.title));
        assertThat(result.size()).isEqualTo(4);

        assertThat(result.get(0).getRate()).isEqualTo(9.5);
        assertThat(result.get(1).getRate()).isEqualTo(8.5);
        assertThat(result.get(2).getRate()).isEqualTo(7.5);
        assertThat(result.get(3).getRate()).isEqualTo(6.5);
    }

    @Test
    void getFilmById_returnsFilmWithCorrectRating() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 10);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 5); // total 7.5

        Film byId = filmService.getById(EXISTING_FILM_ID);
        assertThat(byId).isNotNull();
        assertThat(byId.getRate()).isEqualTo(7.5);
    }

    @Test
    void getFilmsByDirectorId_SortByLikes_returnsCorrectList() {
        List<Director> directors = new ArrayList<>();
        directors.add(directorStorage.getDirectorById(EXISTING_DIRECTOR_ID));
        Film film = filmService.getById(EXISTING_FILM_ID);
        Film film2 = filmService.getById(EXISTING_FILM_2_ID);
        film.setDirectors(directors);
        film2.setDirectors(directors);
        filmService.update(film);
        filmService.update(film2);

        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 10);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 5); // total 7.5

        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_USER_ID, 4);
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 10); // total 7.0

        List<Film> films = new ArrayList<>(filmService.getFilmsByDirectorId(EXISTING_DIRECTOR_ID, SORT_BY_LIKES));

        assertThat(films.size()).isEqualTo(2);
        assertThat(films.get(0).getId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(films.get(1).getId()).isEqualTo(EXISTING_FILM_2_ID);
    }

    @Test
    void getRecommendations_marksOfOtherUsersMakeNoDifference() {
        User created = userService.create(new User("email@email.com", "login", "name", LocalDate.of(1990, 10, 10)));

        // common likes
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 10);

        // Good marks by user 2
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 6);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID, 8);
        // bad marks by user 3 -> will make rating of film 2 = 3.5
        filmService.putLike(EXISTING_FILM_2_ID, created.getId(), 1);

        List<Film> recommendations = filmService.getRecommendations(EXISTING_USER_ID);
        assertThat(recommendations.size()).isEqualTo(2);
        assertThat(recommendations.get(0).getId()).isEqualTo(EXISTING_FILM_3_ID);
        assertThat(recommendations.get(1).getId()).isEqualTo(EXISTING_FILM_2_ID);
    }

    @Test
    void getRecommendations_returnsEmptyList_whenOneUserLiked_otherDisliked_commonFilm() {
        // common likes
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 5);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 10);
        // Good marks by user 2
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 10);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID, 8);
        // bad marks by user 2
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_FRIEND_ID, 1);
        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_FRIEND_ID, 5);

        List<Film> recommendations = filmService.getRecommendations(EXISTING_USER_ID);
        assertThat(recommendations).isEmpty();
    }

    @Test
    void getRecommendations_returnsEmptyList_whenNoFilmsWithPositiveRating() {
        // common likes
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 10);
        // bad marks by user 2
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_FRIEND_ID, 1);
        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_FRIEND_ID, 5);

        List<Film> recommendations = filmService.getRecommendations(EXISTING_USER_ID);
        assertThat(recommendations).isEmpty();
    }

    @Test
    void getRecommendations_returnsEmptyList_whenNoRecommendedFilms() {
        // common likes
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 10);

        List<Film> recommendations = filmService.getRecommendations(EXISTING_USER_ID);
        assertThat(recommendations).isEmpty();
    }

    @Test
    void getRecommendations_returnsFilmsWithPositiveRating_ifThereAreFilmsWithPositiveRating() {
        // common likes
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID, 9);
        filmService.putLike(EXISTING_FILM_ID, EXISTING_FRIEND_ID, 10);
        // Good marks by user 2
        filmService.putLike(EXISTING_FILM_2_ID, EXISTING_FRIEND_ID, 10);
        filmService.putLike(EXISTING_FILM_3_ID, EXISTING_FRIEND_ID, 8);
        // bad marks by user 2
        filmService.putLike(EXISTING_FILM_4_ID, EXISTING_FRIEND_ID, 1);
        filmService.putLike(EXISTING_FILM_5_ID, EXISTING_FRIEND_ID, 5);

        List<Film> recommendations = filmService.getRecommendations(EXISTING_USER_ID);
        assertThat(recommendations.size()).isEqualTo(2);
        assertThat(recommendations.get(0).getId()).isEqualTo(EXISTING_FILM_2_ID);
        assertThat(recommendations.get(1).getId()).isEqualTo(EXISTING_FILM_3_ID);
    }

    @Test
    void whenRemoveDirector_getFilmsByDirectorId_returnNotFound() {
        List<Director> directors = new ArrayList<>();
        directors.add(directorStorage.getDirectorById(EXISTING_DIRECTOR_ID));
        Film film = filmService.getById(EXISTING_FILM_ID);
        film.setDirectors(directors);
        directorStorage.delete(EXISTING_DIRECTOR_ID);

        assertThrows(DirectorIdException.class, () -> filmService.getFilmsByDirectorId(EXISTING_DIRECTOR_ID, SORT_BY_YEAR));

    }

    @Test
    void getFilmsByDirectorId_returnSortByYear() {
        List<Director> directors = new ArrayList<>();
        directors.add(directorStorage.getDirectorById(EXISTING_DIRECTOR_ID));
        Film film = filmService.getById(EXISTING_FILM_ID);
        Film film2 = filmService.getById(EXISTING_FILM_2_ID);
        film.setDirectors(directors);
        film2.setDirectors(directors);
        filmService.update(film);
        filmService.update(film2);

        List<Film> films = new ArrayList<>(filmService.getFilmsByDirectorId(EXISTING_DIRECTOR_ID, SORT_BY_YEAR));

        assertThat(films.size()).isEqualTo(2);
        assertThat(films.get(0).getId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(films.get(1).getId()).isEqualTo(EXISTING_FILM_2_ID);
    }
}