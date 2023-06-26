package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(User user) {
        log.info("Сохранение пользователя {}", user);
        if (user.getId() == 0) {
            KeyHolder holder = new GeneratedKeyHolder();
            String sql = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) " + "VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"USER_ID"});
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setString(4, user.getBirthday().toString());
                return ps;
            }, holder);

            user.setId(Objects.requireNonNull(holder.getKey()).intValue());
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
    public void delete(Integer userId) {
        String sql = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sql, userId);
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
}
