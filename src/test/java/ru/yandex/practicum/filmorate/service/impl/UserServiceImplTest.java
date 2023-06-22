package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.Exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.FriendsService;
import ru.yandex.practicum.filmorate.storage.FilmLikeDao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest extends BaseTest {

    private final UserServiceImpl userService;
    private final FilmLikeDao likeDao;
    private final FriendsService friendsService;

    @Test
    void whenDeleteUserWithLikesFriends_user_deleted() {
        User userById = userService.getUserById(EXISTING_USER_ID);
        likeDao.putLike(EXISTING_FILM_ID, userById.getId());
        friendsService.addFriend(EXISTING_USER_ID, EXISTING_FRIEND_ID);
        userService.deleteUser(EXISTING_USER_ID);
        assertThrows(
                UserIdException.class,
                () -> userService.getUserById(EXISTING_USER_ID)
        );
    }

}