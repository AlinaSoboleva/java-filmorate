package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
public class GenreStorage {
    private final Logger log = LoggerFactory.getLogger(GenreStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createGenreByFilm(int genreId, int filmId) {
        String sql = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) VALUES ( ?, ? )";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    public void deleteAllGenresByFilm(int filmId) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }

    public List<Genre> getGenresByFilm(int filmId) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?);";
        return jdbcTemplate.query(sql, new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Genre(rs.getInt("genre_id"), rs.getString("name"));
            }
        }, filmId);
    }

    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sql, (((rs, rowNum) -> makeGenre(rs))));
    }

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

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }

    public void validationId(Integer id) {
        String sql = "SELECT COUNT(*) FROM GENRES WHERE GENRE_ID = ?";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);
        if (resultSet.next()) {
            if (resultSet.getInt("count(*)") == 0) {
                throw new FilmIdException(String.format("Жанр с id %s не существует", id));
            }
        }
    }
}
