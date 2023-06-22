package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.ReviewsLikeDao;


@Slf4j
@Component
public class ReviewsLikeStorage implements ReviewsLikeDao {

    private final JdbcTemplate jdbcTemplate;

    public ReviewsLikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void putLike(Integer reviewId, Integer userId) {
        if (isEmpty(reviewId, userId)) {
            String sql = "MERGE INTO REVIEWS_LIKES KEY (REVIEW_ID, USER_ID, IS_LIKE)  VALUES (?, ?, ?);";
            jdbcTemplate.update(sql,  reviewId, userId, true);
            addReviewUseful(reviewId);
            log.info("Пользователь с id {} поставл лайк отзыву с id {}", userId,reviewId);
        }
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_LIKE = false";
        jdbcTemplate.update(sql, reviewId, userId);
        addReviewUseful(reviewId);
        log.info("Пользователь с id {} удалил дизлайк отзыву с id {}", userId,reviewId);
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_LIKE = true";
        jdbcTemplate.update(sql, reviewId, userId);
        reduceReviewUseful(reviewId);
        log.info("Пользователь с id {} удалил лайк отзыву с id {}", userId,reviewId);

    }

    @Override
    public void putDislike(Integer reviewId, Integer userId) {
        if (isEmpty(reviewId, userId)) {
            String sql = "MERGE INTO REVIEWS_LIKES KEY (REVIEW_ID, USER_ID,IS_LIKE)  VALUES (?, ?, ?);";
            jdbcTemplate.update(sql,  reviewId, userId, false);
            reduceReviewUseful(reviewId);
            log.info("Пользователь с id {} поставл дизлайк отзыву с id {}", userId,reviewId);
        }
    }

    private boolean isEmpty(Integer reviewId, Integer userID){
        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet("SELECT * FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?", reviewId, userID);
        if (reviewRow.next()){
            return false;
        }
        return true;
    }

    private void addReviewUseful(Integer reviewId){
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE ID=?";
        jdbcTemplate.update(sql, reviewId);
    }

    private void reduceReviewUseful(Integer reviewId){
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE ID=?";
        jdbcTemplate.update(sql, reviewId);
    }
}
