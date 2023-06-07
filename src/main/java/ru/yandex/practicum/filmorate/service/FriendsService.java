package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;

import java.util.List;

@Service
public class FriendsService {

    private final FriendStorage friendStorage;

    public FriendsService(FriendStorage friendStorage) {
        this.friendStorage = friendStorage;
    }

    public void deleteFriend(Integer id, Integer friendId) {
        friendStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriendsList(Integer userId) {
        return friendStorage.getFriendsList(userId);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return friendStorage.getCommonFriends(id, otherId);
    }

    public void addFriend(Integer id, Integer otherId) {
        friendStorage.addFriend(id, otherId);
    }
}
