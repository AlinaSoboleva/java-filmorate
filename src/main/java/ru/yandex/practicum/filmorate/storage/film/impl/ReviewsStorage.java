package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.ReviewsIdException;
import ru.yandex.practicum.filmorate.model.film.Review;
import ru.yandex.practicum.filmorate.storage.film.ReviewsDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class ReviewsStorage implements ReviewsDao {

    private final JdbcTemplate jdbcTemplate;

    public ReviewsStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review create(Review review) {
        log.info("Сохранение отзыва {}", review);
        if (review.getReviewId() == 0) {
            KeyHolder holder = new GeneratedKeyHolder();
            String sql = "INSERT INTO REVIEWS(CONTENT, IS_POSITIVE, USER_ID, FILM_ID) " + "VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
                ps.setString(1, review.getContent());
                ps.setBoolean(2, review.getIsPositive());
                ps.setInt(3, review.getUserId());
                ps.setInt(4, review.getFilmId());
                return ps;
            }, holder);

            review.setReviewId(Objects.requireNonNull(holder.getKey()).intValue());
            log.info("Отзыв {} сохранен", review);
        }
        return review;
    }

    @Override
    public boolean update(Review review) {
        try {
            validationId(review.getReviewId());
        } catch (ReviewsIdException e) {
            return false;
        }
        String sql = "UPDATE REVIEWS SET  CONTENT = ?, IS_POSITIVE = ?" +
                "                WHERE ID = ?;";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return true;
    }

    @Override
    public Review getById(int id) {
        validationId(id);
        String sql = "SELECT * FROM REVIEWS WHERE ID = ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeReview(rs)), id).get(0);
    }

    @Override
    public List<Review> getReviews(int filmId, int count) {
        if (filmId == 0) {
            log.info("Получить {} отзывов ко всем фильмам", count);
            return findAll(count);
        }
        log.info("Получить {} отзывов к фильму с ID {}", count, filmId);
        return findAllByFilmId(filmId, count);
    }

    @Override
    public void validationId(Integer id) {
        String sql = "SELECT COUNT(*) FROM REVIEWS WHERE ID=?";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);
        if (resultSet.next()) {
            if (resultSet.getInt("count(*)") == 0) {
                throw new ReviewsIdException(String.format("Отзыва с id %s не существует", id));
            }
        }
    }

    private List<Review> findAllByFilmId(int filmId, int count) {
        String sql = "SELECT * FROM REVIEWS WHERE FILM_ID IN (?) LIMIT ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) ->
                makeReview(rs)), filmId, count);
    }

    private List<Review> findAll(int count) {
        String sql = "SELECT * FROM REVIEWS LIMIT ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) ->
                makeReview(rs)), count);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        Review review = new Review(rs.getString("content"),
                rs.getBoolean("is_positive"), rs.getInt("user_id"),
                rs.getInt("film_id"), rs.getInt("useful"));
        review.setReviewId(rs.getInt("id"));
        return review;
    }
}
