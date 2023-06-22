package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDao;
import ru.yandex.practicum.filmorate.storage.film.MpaDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaStorage;

    private final GenreDao genreStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Film> findAllTopFilms(Integer count) {
        String sql = "SELECT * FROM FILMS LEFT JOIN  LIKES L on FILMS.FILM_ID = L.FILM_ID " + "GROUP BY FILMS.FILM_ID ORDER BY COUNT(L.FILM_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), count);
    }

    @Override
    public List<Film> getRecommendations(Integer id) {
        String maxUserIntersection = " (SELECT l.user_id u_id, " +
                "COUNT(l.user_id) cnt " +
                "FROM Likes l WHERE l.user_id <> ? " + // ID остальных пользователей
                "AND l.film_id IN (" + // которые поставили лайки тем же фильмам, что и пользователь в запросе
                "SELECT ll.film_id FROM " +
                " Likes ll WHERE ll.user_id = ?)" +
                "GROUP BY l.user_id " + // группировка, так как используем аггрегирующую функцию
                "ORDER BY cnt DESC " + // сортируем по убыванию
                "LIMIT 1) its "; // выбираем максимальное совпадение

        String recommendedFilmsSql = "SELECT * FROM Films fm " + // все фильмы
                "LEFT JOIN Likes lk ON fm.film_id = lk.film_id " +
                "WHERE lk.user_id IN (" + // которые пролайкал пользователь с максимальным пересечением по лайкам
                "SELECT u_id FROM " + maxUserIntersection + ") " +
                "AND lk.film_id NOT IN (" + // и которым наш пользователь не ставил лайк
                "SELECT llk.film_id FROM LIKES llk " +
                "WHERE llk.user_id = ?)";
        return jdbcTemplate.query(recommendedFilmsSql, (rs, rowNum) -> makeFilm(rs), id, id, id);
    }

    @Override
    public void create(Film film) {
        log.info("Сохранение фильма {}", film);
        if (film.getId() == 0) {
            KeyHolder holder = new GeneratedKeyHolder();
            film.setMpa(mpaStorage.getById(film.getMpa().getId()));
            String sql = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " + "VALUES (?, ?, ?, ?,?)";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"FILM_ID"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setString(3, film.getReleaseDate().toString());
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, holder);

            film.setId(Objects.requireNonNull(holder.getKey()).intValue());
            Set<Genre> genres = film.getGenres();
            for (Genre genre : genres) {
                film.getGenres().remove(genre);
                film.getGenres().add(genreStorage.getById(genre.getId()));
                genreStorage.createGenreByFilm(genre.getId(), film.getId());
            }
            log.info("Фильм   {} сохранен", film);
        }
    }

    @Override
    public boolean update(Film film) {
        try {
            validationId(film.getId());
        } catch (FilmIdException e) {
            return false;
        }
        String sql = "UPDATE FILMS SET  NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?,DURATION=?,RATING_ID=?" + "                WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        setMpa(film, film.getMpa().getId());
        Set<Genre> genres = film.getGenres();
        genreStorage.deleteAllGenresByFilm(film.getId());
        for (Genre genre : genres) {
            genreStorage.createGenreByFilm(genre.getId(), film.getId());
        }
        return true;
    }

    @Override
    public void delete(Film film) {
        String sql = "DELETE FROM FILMS WHERE FILM_ID=?";
        jdbcTemplate.update(sql, film.getId());
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)));
    }

    @Override
    public Film getById(Integer id) {
        validationId(id);
        String sql = "SELECT * FROM FILMS WHERE film_id=?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), id).get(0);
    }

    @Override
    public void validationId(Integer id) {
        String sql = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID=?";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);
        if (resultSet.next()) {
            if (resultSet.getInt("count(*)") == 0) {
                throw new FilmIdException(String.format("Фильм с id %s не существует", id));
            }
        }
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("film_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        int mpa = resultSet.getInt("rating_id");
        Film film = new Film(name, description, releaseDate, duration);
        film.setId(id);
        setMpa(film, mpa);
        setGenre(film);
        return film;
    }

    private void setMpa(Film film, int mpa) {
        film.setMpa(mpaStorage.getById(mpa));
    }

    private void setGenre(Film film) {
        film.getGenres().clear();
        film.getGenres().addAll(genreStorage.getGenresByFilm(film.getId()));
    }
}