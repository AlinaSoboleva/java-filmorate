package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.Friends;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.FriendDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FriendStorage implements FriendDao {

    private final JdbcTemplate jdbcTemplate;

    private final UserStorage userStorage;

    public FriendStorage(JdbcTemplate jdbcTemplate, UserDbStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public int deleteFriend(Integer id, Integer friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        int rowsChanged = jdbcTemplate.update(sql, id, friendId);
        log.info("Пользователь с id {} удаляет пользователя с id {}", id, friendId);
        return rowsChanged;
    }

    @Override
    public List<User> getFriendsList(Integer userId) {
        userStorage.validationId(userId);
        String sql = "select * from FRIENDS where USER_ID = ?";
        List<Friends> friends = jdbcTemplate.query(sql, (rs, rowNum) -> makeFollow(rs), userId);

        List<User> friend = friends.stream().map(Friends::getFriendId).map(userStorage::getById).collect(Collectors.toList());

        if (friend.isEmpty()) {
            return Collections.emptyList();
        }
        return friend;
    }

    @Override
    public List<Integer> getCommonFriends(Integer id, Integer otherId) {
        log.info("Список общих друзей пользователей с id {} и {}", id, otherId);
        userStorage.validationId(id);
        userStorage.validationId(otherId);
        String sql = "SELECT FRIEND_ID " +
                "FROM friends " +
                "WHERE  user_id = ? " +
                "INTERSECT " +
                "SELECT FRIEND_ID " +
                "FROM friends " +
                "WHERE  user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), id, otherId);
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        log.info("Пользователь с id {} добавляет пользователя с id {}", id, friendId);
        userStorage.validationId(id);
        userStorage.validationId(friendId);
        String sql = "MERGE INTO FRIENDS KEY (USER_ID, FRIEND_ID, FRIENDS_STATUS) VALUES ( ?, ?, ? )";
        jdbcTemplate.update(sql, id, friendId, false);
        if (checkFriends(id, friendId)) {
            jdbcTemplate.update("UPDATE FRIENDS SET FRIENDS_STATUS=true " + "WHERE USER_ID IN (?,?) AND FRIEND_ID IN (?, ?)", id, friendId, friendId, id);
            log.info("Статус дружбы пользователей {} и {} = true", id, friendId);
        }
    }

    private boolean checkFriends(int userId, int friendId) {
        String sql = "SELECT COUNT(*) FROM FRIENDS WHERE USER_ID IN (?, ?) AND FRIEND_ID IN (?, ?);";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, userId, friendId, friendId, userId);
        if (resultSet.next()) {
            int result = resultSet.getInt("COUNT(*)");
            return result == 2;
        }
        return false;
    }

    private Friends makeFollow(ResultSet rs) throws SQLException {
        return new Friends(rs.getInt("user_id"), rs.getInt("friend_id"));
    }
}
