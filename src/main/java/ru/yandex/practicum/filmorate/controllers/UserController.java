package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FriendsService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final FriendsService friendsService;
    private final EventFeedService eventFeedService;
    private final FilmService filmService;

    public UserController(UserService userService,
                          FriendsService friendsService,
                          EventFeedService eventFeedService,
                          FilmService filmService) {
        this.userService = userService;
        this.friendsService = friendsService;
        this.eventFeedService = eventFeedService;
        this.filmService = filmService;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        friendsService.deleteFriend(id, friendId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        friendsService.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFried(@PathVariable Integer id, @PathVariable Integer otherId) {
        return friendsService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return friendsService.getFriendsList(id);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable("userId") Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<User> update(@Valid @RequestBody User user) {
        User updatedUser = userService.update(user);
        if (updatedUser == null) {
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}/feed")
    public ResponseEntity<List<Event>> getEventFeed(@PathVariable("id") Integer id) {
        log.info("Received GET request for event feed of user with id={}", id);
        return ResponseEntity.ok(eventFeedService.getEventFeedForUser(id));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Integer userId) {
        log.info("Received request to delete user with id={}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Integer id) {
        log.debug("Получен Get запрос users/{id}/recommendations на получение " +
                "списка рекомендуемых фильмов для пользователя с Id: {}", id);
        return filmService.getRecommendations(id);
    }
}
