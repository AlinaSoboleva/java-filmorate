package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void deleteFriend(Integer id, Integer friendId) {
        InMemoryUserStorage.validationId(id);
        InMemoryUserStorage.validationId(friendId);
        InMemoryUserStorage.getUsers().get(id).getFriends().remove(friendId);
        InMemoryUserStorage.getUsers().get(friendId).getFriends().remove(id);
    }

    public List<User> getCommonFried(Integer id, Integer otherId) {
        InMemoryUserStorage.validationId(id);
        InMemoryUserStorage.validationId(otherId);
        List<User> friends = getFriendsList(getUserById(id).getFriends());
        List<User> friends2 = getFriendsList(getUserById(otherId).getFriends());
        friends.retainAll(friends2);
        return friends;
    }

    public List<User> getFriendsList(Set<Integer> friendsId) {
        return userStorage.getFriends(friendsId);
    }

    public void addFriend(Integer id, Integer friendId) {
        InMemoryUserStorage.validationId(id);
        InMemoryUserStorage.validationId(friendId);
        InMemoryUserStorage.getUsers().get(id).getFriends().add(friendId);
        InMemoryUserStorage.getUsers().get(friendId).getFriends().add(id);
    }

    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", InMemoryUserStorage.getUsers().size());
        return InMemoryUserStorage.getUsers().values();
    }

    public ResponseEntity<User> create(User user) {
        userStorage.create(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<User> update(User user) {
        if (userStorage.update(user)) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        log.debug("Пользователь с id: {} не найден", user.getId());
        return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
    }

    public User getUserById(Integer id) {
        return userStorage.getById(id);
    }
}
