package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.FriendsService;
import ru.yandex.practicum.filmorate.storage.user.FriendDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.FriendStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendsServiceImpl implements FriendsService {

    private final FriendDao friendStorage;
    private final UserStorage userStorage;
    private final EventFeedService eventFeedService;

    public FriendsServiceImpl(FriendStorage friendStorage,
                              @Qualifier("userDbStorage") UserStorage userStorage,
                              EventFeedService eventFeedService) {
        this.friendStorage = friendStorage;
        this.userStorage = userStorage;
        this.eventFeedService = eventFeedService;
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        int rowsChanged = friendStorage.deleteFriend(id, friendId);
        if (isFriendDeleted(rowsChanged)) {
            eventFeedService.saveEvent(EventType.FRIEND,
                    EventOperation.REMOVE,
                    id,
                    friendId);
        }
    }

    private boolean isFriendDeleted(int rowsChanged) {
        return rowsChanged != 0;
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
        eventFeedService.saveEvent(EventType.FRIEND,
                EventOperation.ADD,
                id,
                otherId);
    }
}
