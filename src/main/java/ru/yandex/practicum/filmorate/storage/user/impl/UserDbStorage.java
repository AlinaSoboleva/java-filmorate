package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private int id;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(User user) {
        log.info("Сохранение пользователя {}", user);
        if (user.getId() == 0) {
            user.setId(getId());
            String sql = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) " + "VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
            log.info("Пользователь {}  сохранен", user);
        }
    }

    @Override
    public boolean update(User user) {
        try {
            validationId(user.getId());
        } catch (UserIdException e) {
            return false;
        }
        String sql = "UPDATE USERS SET  EMAIL = ?, LOGIN = ?, NAME = ?,BIRTHDAY= ?" + "WHERE USER_ID = ?;";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return true;
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sql, user.getId());
    }

    @Override
    public User getById(Integer id) {
        validationId(id);
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeUser(rs)), id).get(0);
    }

    @Override
    public Collection<User> getUsers() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeUser(rs)));
    }

    @Override
    public void validationId(Integer id) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, id);
        if (resultSet.next()) {
            if (resultSet.getInt("count(*)") == 0) {
                throw new UserIdException(String.format("Пользователь с id %s не существует", id));
            }
        }

    }

    private User makeUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("user_id");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        User user = new User(email, login, name, birthday);
        user.setId(id);
        return user;
    }

    private int getId() {
        return ++id;
    }
}
