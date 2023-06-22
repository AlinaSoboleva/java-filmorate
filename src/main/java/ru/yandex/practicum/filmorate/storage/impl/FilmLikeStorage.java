package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FilmLikeDao;

@Slf4j
@Component
public class FilmLikeStorage implements FilmLikeDao {
    private final JdbcTemplate jdbcTemplate;

    public FilmLikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void putLike(Integer filmId, Integer userId) {
        String sql = "MERGE INTO LIKES KEY (FILM_ID, USER_ID) VALUES (?, ?);";
        jdbcTemplate.update(sql, filmId, userId);
    }
}
