package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreStorage implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createGenreByFilm(int genreId, int filmId) {
        String sql = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) VALUES ( ?, ? )";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void deleteAllGenresByFilm(int filmId) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Genre> getGenresByFilm(int filmId) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?);";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")), filmId);
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sql, (((rs, rowNum) -> makeGenre(rs))));
    }

    @Override
    public Genre getById(int id) {
        validationId(id);
        String sql = "select * from GENRES where GENRE_ID = ?";
        List<Genre> genreList = jdbcTemplate.query(sql, ((rs, rowNum) -> makeGenre(rs)), id);
        if (genreList.isEmpty()) {
            log.info("Жанр с id {} не найден", id);
            return null;
        } else {
            log.info("Найден жанр {}", genreList.get(0).getName());
            return genreList.get(0);
        }
    }

    @Override
    public void validationId(Integer id) {
        String sql = "SELECT COUNT(*) FROM GENRES WHERE GENRE_ID = ?";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);
        if (resultSet.next()) {
            if (resultSet.getInt("count(*)") == 0) {
                throw new FilmIdException(String.format("Жанр с id %s не существует", id));
            }
        }
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }
}
