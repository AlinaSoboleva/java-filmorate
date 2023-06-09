package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface FriendDao {

    void deleteFriend(Integer id, Integer friendId);

    List<User> getFriendsList(Integer userId);

    List<Integer> getCommonFriends(Integer id, Integer otherId);

    void addFriend(Integer id, Integer friendId);

}
