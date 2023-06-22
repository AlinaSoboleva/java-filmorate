package ru.yandex.practicum.filmorate.storage.director.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.DirectorIdException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Director> findAll() {
        String sql = "select * from DIRECTORS";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeDirector(rs)));
    }

    @Override
    public Director getDirectorById(int id) {
        validationId(id);
        String sql = "select * from DIRECTORS where DIRECTOR_ID = ?";
        List<Director> directorsList = jdbcTemplate.query(sql, ((rs, rowNum) -> makeDirector(rs)), id);
        if (directorsList.isEmpty()) {
            log.info("Режиссёр с id {} не найден", id);
            return null;
        } else {
            log.info("Найден режиссёр {}", directorsList.get(0).getName());
            return directorsList.get(0);
        }
    }

    @Override
    public Director create(Director director) {
        log.info("Сохранение режиссёра {}", director);

        KeyHolder holder = new GeneratedKeyHolder();
        String sql = "INSERT INTO DIRECTORS(NAME) " + "VALUES (?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"DIRECTOR_ID"});
            ps.setString(1, director.getName());
            return ps;
        }, holder);

        director.setId(Objects.requireNonNull(holder.getKey()).intValue());
        log.info("Режиссёр {}  сохранен", director);

        return director;
    }

    @Override
    public Boolean update(Director director) {
        try {
            validationId(director.getId());
        } catch (DirectorIdException e) {
            return false;
        }
        String sql = "UPDATE DIRECTORS SET NAME = ?" + "WHERE DIRECTOR_ID = ?;";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return true;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteDirectorByFilm(Film film) {
        String sql = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, film.getId());
    }

    @Override
    public List<Director> getDirectorsByFilm(int filmId) {
        String sql = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID IN (SELECT DIRECTOR_ID FROM FILM_DIRECTOR WHERE FILM_ID = ?);";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Director(rs.getInt("DIRECTOR_ID"), rs.getString("name")), filmId);
    }

    @Override
    public void setDirectorsInDb(Film film) {
        List<Director> directors = film.getDirectors();
        String sql = "MERGE INTO FILM_DIRECTOR KEY (FILM_ID, DIRECTOR_ID) VALUES (?, ?);";
        for (Director director : directors) {
            jdbcTemplate.update(sql, film.getId(), director.getId());
        }
    }

    @Override
    public void validationId(Integer id) {
        String sql = "SELECT COUNT(*) FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);
        if (resultSet.next()) {
            if (resultSet.getInt("count(*)") == 0) {
                throw new DirectorIdException(String.format("Режиссёр с id %s не существует", id));
            }
        }
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return new Director(rs.getInt("director_id"), rs.getString("name"));
    }
}
