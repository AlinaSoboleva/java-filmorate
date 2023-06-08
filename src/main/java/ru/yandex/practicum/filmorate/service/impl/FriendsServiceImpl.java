package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.FriendsService;
import ru.yandex.practicum.filmorate.storage.user.FriendDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendsServiceImpl implements FriendsService {

    private final FriendDao friendStorage;
    private final UserStorage userStorage;

    public FriendsServiceImpl(FriendStorage friendStorage, UserDbStorage userStorage) {
        this.friendStorage = friendStorage;
        this.userStorage = userStorage;
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        friendStorage.deleteFriend(id, friendId);
    }

    @Override
    public List<User> getFriendsList(Integer userId) {
        return friendStorage.getFriendsList(userId);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<Integer> friendsId = friendStorage.getCommonFriends(id, otherId);
        return friendsId.stream().map(userStorage::getById).collect(Collectors.toList());
    }

    @Override
    public void addFriend(Integer id, Integer otherId) {
        friendStorage.addFriend(id, otherId);
    }
}
