package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface FriendsService {

    void deleteFriend(Integer id, Integer friendId);

    List<User> getFriendsList(Integer userId);

    List<User> getCommonFriends(Integer id, Integer otherId);

    void addFriend(Integer id, Integer otherId);

}
